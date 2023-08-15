package vanadium.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.MapColor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import vanadium.adapters.*;
import vanadium.models.*;
import vanadium.models.enums.Format;
import vanadium.models.exceptions.InvalidColorMappingException;
import vanadium.models.records.PropertyImage;
import vanadium.models.records.VanadiumColor;

import java.io.*;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class GsonUtils {
    public static final Gson PROPERTY_GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new StringIdentifiableTypeAdapterFactory())
            .registerTypeAdapter(Identifier.class, new IdentifierAdapter())
            .registerTypeAdapter(ApplicableBlockStates.class, new ApplicableBlockStatesAdapter())
            .registerTypeAdapter(VanadiumColor.class, new VanadiumColorAdapter())
            .registerTypeAdapter(MapColor.class, new MaterialColorAdapter())
            .registerTypeAdapter(Formatting.class, new ChatFormatAdapter())
            .registerTypeAdapter(GridEntry.class, new GridEntryAdapter())
            .create();

    public static String resolveRelativeResourceLocation(String path, Identifier identifier) {
        if(path.startsWith("./")) {
            String thisPath = identifier.toString();
            path = thisPath.substring(0, thisPath.lastIndexOf('/')) + path.substring(1);
        } else if(path.startsWith("~/")) {
            path = "optifine" + path.substring(1);
        } else if(!path.contains("/") && !path.contains(":")) {
            String thisPath = identifier.toString();
            path = thisPath.substring(0, thisPath.lastIndexOf('/') + 1) + path;
        }
        return path;
    }

    public static Reader getJsonReader(InputStream inputStream,
                                       Identifier identifier,
                                       Function<String, String> keyMapper,
                                       Predicate<String> arrayValues) throws IOException {
        if (!identifier
                .getPath()
                .endsWith(".properties")) {
            return new InputStreamReader(inputStream);
        }

        Properties properties = new Properties();
        properties.load(inputStream);
        return new StringReader(GsonUtils.toJsonString(properties, keyMapper, arrayValues));

    }

    public static PropertyImage loadColorMapping(ResourceManager resourceManager, Identifier identifier, boolean isCustom) {
        ColorMappingProperties properties = ColorMappingProperties.load(resourceManager, identifier,isCustom);

        if(properties.getFormat() == Format.FIXED) {
            return new PropertyImage(properties, null);
        }

        try(InputStream inputStream = resourceManager.getResourceOrThrow(properties.getSource()).getInputStream()) {
            NativeImage image = NativeImage.read(inputStream);
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                IntStream
                        .range(0, image.getWidth())
                        .forEach(xPixelCoord ->
                                 IntStream
                                         .range(0, image.getHeight())
                                         .forEach(yPixelCoord -> {
                                             int pixel = image.getColor(xPixelCoord, yPixelCoord);
                                             int temporaryPixel = (pixel &0xff0000) >>16;
                                             temporaryPixel |= (pixel &0x0000ff) <<16;
                                             pixel &= ~(0xff0000 |0x0000ff);
                                             pixel |= temporaryPixel;
                                             image.setColor(xPixelCoord, yPixelCoord, pixel);
                                         }));
            }

            if(properties.getFormat() == Format.VANILLA
                    && (image.getWidth() != 256 || image.getHeight() != 256)) {
                throw new InvalidColorMappingException("Vanilla Colormap dimensions must be 256x256");
            }
            return new PropertyImage(properties, image);
        } catch(IOException e) {
            throw new InvalidColorMappingException(e);
        }
    }

    private static String toJsonString(Properties properties, Function<String, String> keyMappingFunction, Predicate<String> arrayValue) {
        Map<String, Object> propertyMap = properties.stringPropertyNames().stream()
                                                    .collect(Collectors.toMap(
                                                            property -> property,
                                                            property -> {
                                                                String[] keys = property.split("\\.");
                                                                String nestedKey = keyMappingFunction.apply(keys[keys.length -1]);
                                                                String propertyValue = properties.getProperty(property);
                                                                Object value = arrayValue.test(nestedKey)
                                                                        ? propertyValue.split("\\s+")
                                                                        : propertyValue;
                                                                return Map.of(nestedKey, value);
                                                            },
                                                            (value1, value2) -> {
                                                                if (value1 instanceof Object[] && value2 instanceof Object[]) {
                                                                    return Stream
                                                                            .concat(Arrays.stream((Object[]) value1), Arrays.stream((Object[]) value2))
                                                                            .toArray();
                                                                } else {
                                                                    return new Object[]{value1, value2};
                                                                }
                                                            },
                                                            LinkedHashMap::new ));

        return PROPERTY_GSON.toJson(propertyMap);
    }


    private static Object mergeCompoundKeys(Object existingValue, Object newValue) {
        if(existingValue  instanceof Map<?,?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> existingMap = (Map<String, Object>) existingValue;
            existingMap.put("", newValue);
            return existingMap;
        }

        return newValue;
    }
}

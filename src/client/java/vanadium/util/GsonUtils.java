package vanadium.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.MapColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import vanadium.properties.ApplicableBlockStates;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;

public class GsonUtils {
    public static final Gson PROPERTY_GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new StringIdentifiableTypeAdapterFactory())
            .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
            .registerTypeAdapter(ApplicableBlockStates.class, new ApplicableBlockStatesTypeAdapter())
            .registerTypeAdapter(HexColor.class, new HexColorAdapter())
            .registerTypeAdapter(MapColor.class, new MaterialColorAdapter())
            .registerTypeAdapter(Formatting.class, new ChatFormattingAdapter())
            .registerTypeAdapter(GridEntry.class, new GridEntryAdapter())
            .create();

    public static String resolveRelativeIdentifier(String path, Identifier identifier) {
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



    private static String toJsonString(Properties properties, Function<String, String> keyMappingFunction, Predicate<String> arrayValue) {
        Map<String, Object> propertyMap = new HashMap<>();

        for(String property : properties.stringPropertyNames()) {
            String[] keys = property.split("\\.");
            Map<String, Object> nestedProperty = propertyMap;
            int i;

            for(i = 0; i<keys.length - 1; i++) {
                String key = keyMappingFunction.apply(keys[i]);
                Object temporaryComputed = nestedProperty.computeIfAbsent(key, k -> new HashMap<>());

                if(temporaryComputed instanceof Map<?,?>) {
                    nestedProperty = (Map<String, Object>) temporaryComputed;
                } else {
                    Map<String, Object> newNestedProperty = new HashMap<>();
                    newNestedProperty.put("", temporaryComputed);
                    nestedProperty.put(key, newNestedProperty);
                    nestedProperty = newNestedProperty;
                }
            }
            String key = keyMappingFunction.apply(keys[i]);
            String propertyValue = properties.getProperty(property);
            Object value = arrayValue.test(key)
                    ? propertyValue.split("\\s+")
                    : propertyValue;
            nestedProperty.merge(key, value, GsonUtils::mergeCompoundKeys);
        }
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

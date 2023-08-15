package vanadium.customcolors.resources;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.customcolors.mapping.LightMappings;
import vanadium.customcolors.mapping.Lightmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LightMappingResource implements SimpleResourceReloadListener<Map<Identifier, NativeImage>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Identifier identifier;
    private final Identifier optifineResourceLocation;

    public LightMappingResource(Identifier identifier) {
        this.identifier = identifier;
        this.optifineResourceLocation = new Identifier("minecraft", "optifine/" + identifier.getPath());
    }

    @Override
    public Identifier getFabricId() {
        return identifier;
    }

    private static Map<Identifier, NativeImage> getLightMappings(ResourceManager manager, Identifier directoryResourceLocation) {
        Map<Identifier, Resource> filesToMap = manager
                .findResources(directoryResourceLocation.getPath(),
                        s -> s.getPath().endsWith(".png")
                && s.getNamespace().equals(directoryResourceLocation.getNamespace()));

        Map<Identifier, NativeImage> resolvedLightMappings = new HashMap<>(filesToMap.size());

        filesToMap
                .forEach((key, value) -> {
                    try (InputStream inputStream = value.getInputStream()) {
                        int directoryLength = directoryResourceLocation
                                .getPath()
                                .length();
                        String keyResourceLocation = key.toString();
                        String dimensionResourceLocation = keyResourceLocation
                                .substring(directoryLength + 1, keyResourceLocation.length() - 4)
                                .replaceFirst("/", ":");

                        String fixedDimensionResourceLocation = fixOptifineDimensionResourceLocation(dimensionResourceLocation);

                        Identifier dimensionId = Identifier.tryParse(fixedDimensionResourceLocation);

                        if (dimensionId != null) {
                            resolvedLightMappings.put(key, NativeImage.read(inputStream));
                        } else {
                            LOGGER.error("Invalid lightmapping for dimension ID: " + keyResourceLocation);
                        }
                    } catch (IOException e) {
                        LOGGER.error("Failed to read lightmapping file " + key, e);
                    }
                });
        return resolvedLightMappings;
    }

    private static String fixOptifineDimensionResourceLocation(String dimensionResourceLocation) {
        return switch(dimensionResourceLocation){
            case "world0" -> "minecraft:overworld";
            case "world-1" -> "minecraft:the_nether";
            case "world1" -> "minecraft:the_end";
            default -> dimensionResourceLocation;
        };
    }

    @Override
    public CompletableFuture<Map<Identifier, NativeImage>> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Identifier, NativeImage> lightMappings = getLightMappings(manager, optifineResourceLocation);
            lightMappings.putAll(getLightMappings(manager, identifier));
            return lightMappings;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Map<Identifier, NativeImage> data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            LightMappings.clearLightMaps();
            for (Map.Entry<Identifier, NativeImage> entry : data.entrySet()) {
                NativeImage image = entry.getValue();
                if(image.getWidth() < 2
                        || (image.getHeight() != 32
                        && image.getHeight()!= 64)){
                    LOGGER.warn("Lightmapping image dimensions must be either wX32 or wX64" + entry.getKey());
                } else {
                    LightMappings.addLightMap(entry.getKey(), new Lightmap(image));
                }
            }
        }, executor);
    }
}
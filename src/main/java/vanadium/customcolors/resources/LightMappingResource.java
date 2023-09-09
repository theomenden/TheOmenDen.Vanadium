package vanadium.customcolors.resources;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
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

public class LightMappingResource implements SimpleResourceReloadListener<Map<ResourceLocation, NativeImage>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceLocation identifier;
    private final ResourceLocation optifineResourceLocation;

    public LightMappingResource(ResourceLocation identifier) {
        this.identifier = identifier;
        this.optifineResourceLocation = new ResourceLocation("minecraft", "optifine/" + identifier.getPath());
    }
    @Override
    public ResourceLocation getFabricId() {
        return identifier;
    }
    private static Map<ResourceLocation, NativeImage> getLightMappings(ResourceManager manager, ResourceLocation directoryResourceLocation) {
        Map<ResourceLocation, Resource> filesToMap = manager
                .listResources(directoryResourceLocation.getPath(),
                        s -> s.getPath().endsWith(".png")
                                && s.getNamespace().equals(directoryResourceLocation.getNamespace()));

        Map<ResourceLocation, NativeImage> resolvedLightMappings = new HashMap<>(filesToMap.size());

        filesToMap
                .forEach((key, value) -> {
                    try (InputStream inputStream = value.open()) {
                        int directoryLength = directoryResourceLocation
                                .getPath()
                                .length();
                        String keyResourceLocation = key.toString();
                        String dimensionResourceLocation = keyResourceLocation
                                .substring(directoryLength + 1, keyResourceLocation.length() - 4)
                                .replaceFirst("/", ":");

                        String fixedDimensionResourceLocation = fixOptifineDimensionResourceLocation(dimensionResourceLocation);

                        ResourceLocation dimensionId = ResourceLocation.tryParse(fixedDimensionResourceLocation);

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
    public CompletableFuture<Map<ResourceLocation, NativeImage>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, NativeImage> lightMappings = getLightMappings(manager, optifineResourceLocation);
            lightMappings.putAll(getLightMappings(manager, identifier));
            return lightMappings;
        }, executor);
    }
    @Override
    public CompletableFuture<Void> apply(Map<ResourceLocation, NativeImage> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            LightMappings.clearLightMaps();
            for (Map.Entry<ResourceLocation, NativeImage> entry : data.entrySet()) {
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
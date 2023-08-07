package vanadium.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.entry.Vanadium;
import vanadium.colormapping.BiomeColorMap;
import vanadium.colormapping.BiomeColorMappings;
import vanadium.exceptions.InvalidColorMappingException;
import vanadium.models.ColorMapNativePropertyImage;
import vanadium.util.GsonUtils;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomBiomeColorMappingResource implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger(Vanadium.MODID);
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-zAZ0-9_/.-]+");

    private final ResourceLocation identifier;
    private final ResourceLocation optifineIdentifier;
    private final ResourceLocation colormaticIdentifier;
    private final ResourceLocation otherOptifineIdentifier;

    public CustomBiomeColorMappingResource() {
        this.identifier = new ResourceLocation(Vanadium.MODID, "colormap/custom");
        this.colormaticIdentifier = new ResourceLocation(Vanadium.COLORMATIC_ID, "colormap/custom");
        this.optifineIdentifier = new ResourceLocation("minecraft", "optifine/colormap/custom");
        this.otherOptifineIdentifier = new ResourceLocation("minecraft", "optifine/colormap/blocks");
    }

    @Override
    public ResourceLocation getFabricId() {
        return identifier;
    }


    private static void addColorMappings(ResourceManager manager, ResourceLocation directory, boolean isInJson) {
        String extension = isInJson? ".json" : ".properties";
        Collection<ResourceLocation> files = manager.listResources(directory.getPath(),
                                                      id -> id.getNamespace().equals(directory.getNamespace())
                                                              && (id.getPath().endsWith(extension) || id.getPath().endsWith(".png")))
                                              .keySet()
                                              .stream()
                                              .map(id -> {
                                                  String path = id.getPath();
                                                  if(path.endsWith(".png")) {
                                                      String standardizedPath = path.substring(0, path.length() - 4) + extension;
                                                      return new ResourceLocation(id.getNamespace(), standardizedPath);
                                                  }
                                                  return id;
                                              })
                                              .distinct()
                                              .collect(Collectors.toList());

        files.forEach(file -> {
            if (!IDENTIFIER_PATTERN
                    .matcher(file.getPath())
                    .matches()) {
                LOGGER.warn("Invalid identifier in custom biome color mappings file: " + file);
            }
            try {
                ColorMapNativePropertyImage propertyImage = GsonUtils.loadColorMapping(manager, file, true);
                BiomeColorMap colorMapping = new BiomeColorMap(propertyImage.colormapProperties(), propertyImage.nativeImage());
                BiomeColorMappings.addBiomeColorMapping(colorMapping);
            } catch (InvalidColorMappingException e) {
                LOGGER.error("Unable to parse Colormapping {}:{} ", file, e.getMessage());
            }
        });
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        BiomeColorMappings.resetColorMappings();
        addColorMappings(resourceManager, otherOptifineIdentifier, false);
        addColorMappings(resourceManager, optifineIdentifier, false);
        addColorMappings(resourceManager, colormaticIdentifier, true);
        addColorMappings(resourceManager, identifier, true);
    }
}

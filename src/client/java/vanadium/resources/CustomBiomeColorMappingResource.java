package vanadium.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.Vanadium;
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

    private final Identifier identifier;
    private final Identifier optifineIdentifier;
    private final Identifier colormaticIdentifier;
    private final Identifier otherOptifineIdentifier;

    public CustomBiomeColorMappingResource() {
        this.identifier = new Identifier(Vanadium.MODID, "colormap/custom");
        this.colormaticIdentifier = new Identifier(Vanadium.COLORMATIC_ID, "colormap/custom");
        this.optifineIdentifier = new Identifier("minecraft", "optifine/colormap/custom");
        this.otherOptifineIdentifier = new Identifier("minecraft", "optifine/colormap/blocks");
    }

    @Override
    public Identifier getFabricId() {
        return identifier;
    }

    @Override
    public void reload(ResourceManager manager) {
        BiomeColorMappings.resetColorMappings();
        addColorMappings(manager, otherOptifineIdentifier, false);
        addColorMappings(manager, optifineIdentifier, false);
        addColorMappings(manager, colormaticIdentifier, true);
        addColorMappings(manager, identifier, true);
    }

    private static void addColorMappings(ResourceManager manager, Identifier directory, boolean isInJson) {
        String extension = isInJson? ".json" : ".properties";
        Collection<Identifier> files = manager.findResources(directory.getPath(),
                                                      id -> id.getNamespace().equals(directory.getNamespace())
                                                              && (id.getPath().endsWith(extension) || id.getPath().endsWith(".png")))
                                              .keySet()
                                              .stream()
                                              .map(id -> {
                                                  String path = id.getPath();
                                                  if(path.endsWith(".png")) {
                                                      String standardizedPath = path.substring(0, path.length() - 4) + extension;
                                                      return new Identifier(id.getNamespace(), standardizedPath);
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
}

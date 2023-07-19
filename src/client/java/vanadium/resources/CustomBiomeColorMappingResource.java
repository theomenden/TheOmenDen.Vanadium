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

import static java.util.stream.Collectors.toList;

public class CustomBiomeColorMappingResource implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger(Vanadium.MODID);
    private static final Pattern ID_PATTERN = Pattern.compile("[a-z0-9_/.-]+");

    private final Identifier identifier;
    private final Identifier optifineIdentifier;
    private final Identifier additionalOptifineIdentifier;

    public CustomBiomeColorMappingResource(Identifier identifier) {
        this.identifier = identifier;
        this.optifineIdentifier = new Identifier("minecraft", "optifine/colormap/custom");
        this.additionalOptifineIdentifier = new Identifier("minecraft", "optifine/colormap/blocks");
    }


    @Override
    public Identifier getFabricId() {
        return identifier;
    }

    @Override
    public void reload(ResourceManager manager) {
        BiomeColorMappings.resetColorMappings();
        addColormapping(manager, additionalOptifineIdentifier, false);
        addColormapping(manager, optifineIdentifier, false);
        addColormapping(manager, identifier, true);
    }

    private static void addColormapping(ResourceManager manager, Identifier directory, boolean isJson) {
        String fileExtension = isJson ? ".json" : ".properties";

        Collection<Identifier> filesToScan = manager
                .findResources(directory.getPath(),
                        id -> id
                                .getNamespace()
                                .equals(directory.getNamespace())
                                && (id
                                .getPath()
                                .endsWith(fileExtension)
                                || id
                                .getPath()
                                .endsWith("png")))
                .keySet()
                .stream()
                .map(id -> {
                    String path = id.getPath();
                    if (path.endsWith(".png")) {
                        String newPath = path.substring(0, path.length() - 4) + fileExtension;
                        return new Identifier(directory.getNamespace(), newPath);
                    }
                    return id;
                })
                .distinct()
                .collect(toList());

        filesToScan.forEach(id -> {
            if (!ID_PATTERN
                    .matcher(id.getPath())
                    .matches()) {
                LOGGER.warn("Colormapping definition file '{}' does not name a valid resource location. Check with the resource pack author to create an issue, or for asking for a fix.", id);
            }
            try {
                ColorMapNativePropertyImage pi = GsonUtils.loadColorMapping(manager, id, true);
                BiomeColorMap colorMapping = new BiomeColorMap(pi.colormapProperties(), pi.nativeImage());
                BiomeColorMappings.addBiomeColorMapping(colorMapping);
            } catch (InvalidColorMappingException e) {
                LOGGER.warn("Error parsing {} : {}", id, e.getMessage());
            }
        });
    }
}

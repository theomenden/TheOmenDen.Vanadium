package vanadium.customcolors.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.Vanadium;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.models.exceptions.InvalidColorMappingException;
import vanadium.models.records.PropertyImage;
import vanadium.utils.GsonUtils;

import java.util.Collection;
import java.util.regex.Pattern;

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
                                                    .toList();

        files.forEach(id -> {
            if (!IDENTIFIER_PATTERN
                    .matcher(id.getPath())
                    .matches()) {
                LOGGER.error("Colormapping definition file '{}' does not name a valid resource location. Please have the resource pack author fix this.", id);
            }
            try {
                PropertyImage pi = GsonUtils.loadColorMapping(manager, id, true);
                BiomeColorMapping colorMapping = new BiomeColorMapping(pi.properties(), pi.nativeImage());
                BiomeColorMappings.addBiomeColorMapping(colorMapping);
            } catch (InvalidColorMappingException e) {
                LOGGER.error("Error parsing {}: {}", id, e.getMessage());
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
package vanadium.customcolors.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import vanadium.customcolors.mapping.LightMappingProperties;

public class GlobalLightMappingResource implements SimpleSynchronousResourceReloadListener {
    private final Identifier identifier;
    private LightMappingProperties properties;

    public GlobalLightMappingResource(Identifier identifier) {
        this.identifier = identifier;
    }

    public LightMappingProperties getProperties() {
        return this.properties;
    }

    @Override
    public Identifier getFabricId() {
        return this.identifier;
    }

    @Override
    public void reload(ResourceManager resourceManager) {

        properties = LightMappingProperties.loadPropertiesForIdentifier(resourceManager, this.identifier);
    }
}
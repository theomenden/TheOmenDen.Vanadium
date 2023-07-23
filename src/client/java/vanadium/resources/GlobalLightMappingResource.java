package vanadium.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import vanadium.properties.LightMappingProperties;

public class GlobalLightMappingResource implements SimpleSynchronousResourceReloadListener {
    private final ResourceLocation identifier;
    private LightMappingProperties properties;

    public GlobalLightMappingResource(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public LightMappingProperties getProperties() {
        return this.properties;
    }

    @Override
    public ResourceLocation getFabricId() {
        return this.identifier;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

        properties = LightMappingProperties.loadPropertiesForIdentifier(resourceManager, this.identifier);
    }
}

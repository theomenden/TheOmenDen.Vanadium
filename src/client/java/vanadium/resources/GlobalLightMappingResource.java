package vanadium.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import vanadium.properties.LightMappingProperties;

public class GlobalLightMappingResource implements SimpleSynchronousResourceReloadListener {
    private final Identifier identifier;
    private LightMappingProperties properties;

    public GlobalLightMappingResource(Identifier identifier) {
        this.identifier = identifier;
    }

    public LightMappingProperties getProperties() {
        return properties;
    }

    @Override
    public Identifier getFabricId() {
        return identifier;
    }

    @Override
    public void reload(ResourceManager manager) {
            properties = LightMappingProperties.loadPropertiesForIdentifier(manager, identifier);
    }
}

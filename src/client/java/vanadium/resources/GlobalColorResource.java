package vanadium.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import vanadium.properties.GlobalColorProperties;

public class GlobalColorResource implements SimpleSynchronousResourceReloadListener {
    private final ResourceLocation identifier;
    private final ResourceLocation optifineId;
    private GlobalColorProperties globalColorProperties = GlobalColorProperties.DEFAULT;

    public GlobalColorResource(ResourceLocation identifier) {
        this.identifier = new ResourceLocation(identifier.getNamespace(), identifier.getPath() + ".json");
        this.optifineId = new ResourceLocation("minecraft", "optifine/" + identifier.getPath() + ".properties");
    }

    public GlobalColorProperties getProperties() {
        return globalColorProperties;
    }

    @Override
    public ResourceLocation getFabricId() {
        return identifier;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        GlobalColorProperties properties = GlobalColorProperties.load(resourceManager, identifier, false);

        if(properties == null) {
            properties = GlobalColorProperties.load(resourceManager, optifineId, true);
        }

        this.globalColorProperties = properties;
    }
}

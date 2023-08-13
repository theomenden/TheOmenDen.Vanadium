package vanadium.customcolors.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import vanadium.defaults.GlobalColorProperties;

public class GlobalColorResource implements SimpleSynchronousResourceReloadListener {
    private final Identifier identifier;
    private final Identifier optifineId;
    private GlobalColorProperties globalColorProperties = GlobalColorProperties.DEFAULT;

    public GlobalColorResource(Identifier identifier) {
        this.identifier = new Identifier(identifier.getNamespace(), identifier.getPath() + ".json");
        this.optifineId = new Identifier("minecraft", "optifine/" + identifier.getPath() + ".properties");
    }

    public GlobalColorProperties getProperties() {
        return globalColorProperties;
    }

    @Override
    public Identifier getFabricId() {
        return identifier;
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        GlobalColorProperties properties = GlobalColorProperties.load(resourceManager, identifier, false);

        if(properties == null) {
            properties = GlobalColorProperties.load(resourceManager, optifineId, true);
        }

        this.globalColorProperties = properties;
    }
}
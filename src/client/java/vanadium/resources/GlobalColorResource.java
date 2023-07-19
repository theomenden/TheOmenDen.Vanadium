package vanadium.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import vanadium.properties.GlobalColorProperties;

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
    public void reload(ResourceManager manager) {
        GlobalColorProperties properties = GlobalColorProperties.load(manager, identifier, false);

        if(properties == null) {
            properties = GlobalColorProperties.load(manager, optifineId, true);
        }

        this.globalColorProperties = properties;
    }
}

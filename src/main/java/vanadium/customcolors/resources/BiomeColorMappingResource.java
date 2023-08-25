package vanadium.customcolors.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.models.records.PropertyImage;
import vanadium.models.exceptions.InvalidColorMappingException;
import vanadium.utils.GsonUtils;

public class BiomeColorMappingResource implements SimpleSynchronousResourceReloadListener {
    private final Identifier identifier;
    private final Identifier optifineId;

    private final Identifier colormaticId;

    private BiomeColorMapping mapping;

    public BiomeColorMappingResource(Identifier identifier) {
        this.identifier = new Identifier(identifier.getNamespace(), identifier.getPath() +".json");
        this.optifineId = new Identifier("minecraft", "optifine/" + identifier.getPath() +".properties");
        this.colormaticId = new Identifier(identifier.getNamespace(), "colormatic/" + identifier.getPath() +".json");
    }

    @Override
    public Identifier getFabricId() {
        return this.identifier;
    }

    public boolean hasCustomColorMapping() {
        return this.mapping!= null;
    }

    public BiomeColorMapping getColorMapping() {
        if(this.mapping == null) {
            throw new IllegalStateException("No custom color mapping found" + getFabricId());
        }
        return this.mapping;
    }

    @Nullable
    private PropertyImage checkForOptifineColorMap(ResourceManager manager) {
        try {
            return GsonUtils.loadColorMapping(manager, this.optifineId, false);
        } catch(InvalidColorMappingException e) {
            return null;
        }
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        PropertyImage propertyImage;

        try {
            propertyImage = GsonUtils.loadColorMapping(resourceManager, this.identifier, false);
        } catch(InvalidColorMappingException e) {
            propertyImage = checkForOptifineColorMap(resourceManager);
        }
        this.mapping = propertyImage != null
                ? new BiomeColorMapping(propertyImage.properties(), propertyImage.nativeImage())
                : null;
    }
}
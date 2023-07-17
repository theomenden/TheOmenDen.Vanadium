package vanadium.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import vanadium.colormapping.BiomeColorMap;
import vanadium.exceptions.InvalidColorMappingException;
import vanadium.models.ColorMapNativePropertyImage;
import vanadium.util.GsonUtils;

public class BiomeColorMappingResource implements SimpleSynchronousResourceReloadListener {
    private final Identifier identifier;
    private final Identifier optifineId;
    private BiomeColorMap mapping;

    public BiomeColorMappingResource(Identifier identifier) {
        this.identifier = new Identifier(identifier.getNamespace(), identifier.getPath() +".json");
        this.optifineId = new Identifier("minecraft", "optifine/" + identifier.getPath() +".properties");

    }

    @Override
    public Identifier getFabricId() {
        return this.identifier;
    }

    public boolean hasCustomColorMapping() {
        return this.mapping!= null;
    }

    public BiomeColorMap getColorMapping() {
        if(this.mapping == null) {
            throw new IllegalStateException("No custom color mapping found" + getFabricId());
        }
        return this.mapping;
    }

    @Override
    public void reload(ResourceManager manager) {
        ColorMapNativePropertyImage propertyImage;

        try {
            propertyImage = GsonUtils.loadColorMapping(manager, this.identifier, false);
        } catch(InvalidColorMappingException e) {
            propertyImage = checkForOptifineColorMap(manager);
        }
        this.mapping = propertyImage != null
                ? new BiomeColorMap(propertyImage.colormapProperties(), propertyImage.nativeImage())
                : null;
    }

    @Nullable
    private ColorMapNativePropertyImage checkForOptifineColorMap(ResourceManager manager) {
        try {
            return GsonUtils.loadColorMapping(manager, this.optifineId, false);
        } catch(InvalidColorMappingException e) {
            return null;
        }
    }
}

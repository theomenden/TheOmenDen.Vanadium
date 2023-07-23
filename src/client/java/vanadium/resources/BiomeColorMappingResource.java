package vanadium.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import vanadium.colormapping.BiomeColorMap;
import vanadium.exceptions.InvalidColorMappingException;
import vanadium.models.ColorMapNativePropertyImage;
import vanadium.util.GsonUtils;

public class BiomeColorMappingResource implements SimpleSynchronousResourceReloadListener {
    private final ResourceLocation identifier;
    private final ResourceLocation optifineId;
    private BiomeColorMap mapping;

    public BiomeColorMappingResource(ResourceLocation identifier) {
        this.identifier = new ResourceLocation(identifier.getNamespace(), identifier.getPath() +".json");
        this.optifineId = new ResourceLocation("minecraft", "optifine/" + identifier.getPath() +".properties");

    }

    @Override
    public ResourceLocation getFabricId() {
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

    @Nullable
    private ColorMapNativePropertyImage checkForOptifineColorMap(ResourceManager manager) {
        try {
            return GsonUtils.loadColorMapping(manager, this.optifineId, false);
        } catch(InvalidColorMappingException e) {
            return null;
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        ColorMapNativePropertyImage propertyImage;

        try {
            propertyImage = GsonUtils.loadColorMapping(resourceManager, this.identifier, false);
        } catch(InvalidColorMappingException e) {
            propertyImage = checkForOptifineColorMap(resourceManager);
        }
        this.mapping = propertyImage != null
                ? new BiomeColorMap(propertyImage.colormapProperties(), propertyImage.nativeImage())
                : null;
    }
}

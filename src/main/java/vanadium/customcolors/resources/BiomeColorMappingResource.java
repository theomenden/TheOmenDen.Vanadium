package vanadium.customcolors.resources;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.models.exceptions.InvalidColorMappingException;
import vanadium.models.records.PropertyImage;
import vanadium.utils.GsonUtils;

public class BiomeColorMappingResource implements SimpleSynchronousResourceReloadListener {
    private final ResourceLocation identifier;
    private final ResourceLocation optifineId;

    private final ResourceLocation colormaticId;

    private BiomeColorMapping mapping;

    public BiomeColorMappingResource(ResourceLocation identifier) {
        this.identifier = new ResourceLocation(identifier.getNamespace(), identifier.getPath() +".json");
        this.optifineId = new ResourceLocation("minecraft", "optifine/" + identifier.getPath() +".properties");
        this.colormaticId = new ResourceLocation(identifier.getNamespace(), "colormatic/" + identifier.getPath() +".json");
    }

    @Override
    public ResourceLocation getFabricId() {
        return this.identifier;
    }

    public boolean hasCustomColorMapping() {
        return this.mapping!= null;
    }

    public BiomeColorMapping getColorMapping() {
        if(this.mapping == null) {
            throw new IllegalStateException("No custom color mapping found " + getFabricId());
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

    @Nullable
    private PropertyImage checkForColormaticColorMapping(ResourceManager manager) {
        try {
            return GsonUtils.loadColorMapping(manager, this.colormaticId, false);
        } catch (InvalidColorMappingException e) {
            return null;
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        PropertyImage propertyImage;

        try {
            propertyImage = checkForColormaticColorMapping(resourceManager);
            if(propertyImage == null) {
                GsonUtils.loadColorMapping(resourceManager, this.identifier, false);
            }
        } catch(InvalidColorMappingException e) {
            propertyImage = checkForOptifineColorMap(resourceManager);
        }

        this.mapping = propertyImage != null
                ? new BiomeColorMapping(propertyImage.properties(), propertyImage.nativeImage())
                : null;
    }
}
package vanadium.models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.intellij.lang.annotations.Identifier;
import org.jetbrains.annotations.Nullable;
import vanadium.colormapping.BiomeColorMap;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import vanadium.exceptions.InvalidColorMappingException;
import vanadium.util.GsonUtils;

import java.io.IOException;

public class BiomeColorMappingResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private final ResourceLocation biomeColormapId;
    private final ResourceLocation optifineId;
    private BiomeColorMap colorMapping;

    public BiomeColorMappingResourceReloadListener(ResourceLocation id) {
        this.biomeColormapId = new ResourceLocation(id.getNamespace(), id.getPath() + ".json");
        this.optifineId = new ResourceLocation("minecraft", "optifine/" + id.getPath() + ".properties");
    }

    @Override
    public ResourceLocation getFabricId() {
        return biomeColormapId;
    }

    public BiomeColorMap getColorMapping() {
        if(colorMapping == null) {
            throw new IllegalStateException("A custom Color mapping was not present: " + getFabricId());
        }
        return colorMapping;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        ColorMapNativePropertyImage pi;
        try {
            pi = GsonUtils.loadColorMapping(resourceManager, biomeColormapId, false);
        } catch (InvalidColorMappingException e) {
            pi = tryOptifineDirectory(resourceManager);
        }

        colorMapping = pi == null ? null : new BiomeColorMap(pi.colormapProperties(), pi.nativeImage());
    }

    @Nullable
    private ColorMapNativePropertyImage tryOptifineDirectory(ResourceManager manager) {
        try {
            return GsonUtils.loadColorMapping(manager, optifineId, false);
        } catch(InvalidColorMappingException e) {
            return null;
        }
    }
}

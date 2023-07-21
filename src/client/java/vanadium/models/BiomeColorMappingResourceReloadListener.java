package vanadium.models;

import org.jetbrains.annotations.Nullable;
import vanadium.colormapping.BiomeColorMap;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import vanadium.exceptions.InvalidColorMappingException;
import vanadium.util.GsonUtils;

import java.io.IOException;

public class BiomeColorMappingResourceReloadListener  implements SimpleSynchronousResourceReloadListener {
    private final Identifier biomeColormapId;
    private final Identifier optifineId;
    private BiomeColorMap colorMapping;
    private static final String OPTIFINE_PATH = "optifine/%s.properties";

    public BiomeColorMappingResourceReloadListener(Identifier id) {
        this.biomeColormapId = new Identifier(id.getNamespace(), id.getPath() + ".json");
        this.optifineId = new Identifier("minecraft", "optifine/" + id.getPath() + ".properties");
    }

    @Override
    public Identifier getFabricId() {
        return biomeColormapId;
    }

    public BiomeColorMap getColorMapping() {
        if(colorMapping == null) {
            throw new IllegalStateException("A custom Color mapping was not present: " + getFabricId());
        }
        return colorMapping;
    }

    @Override
    public void reload(ResourceManager manager){
        ColorMapNativePropertyImage pi;
        try {
            pi = GsonUtils.loadColorMapping(manager, biomeColormapId, false);
        } catch (InvalidColorMappingException e) {
           pi = tryOptifineDirectory(manager);
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

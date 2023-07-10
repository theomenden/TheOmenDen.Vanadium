package vanadium.models;

import vanadium.colormapping.BiomeColorMap;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class BiomeColorMappingResourceReloadListener  implements SimpleSynchronousResourceReloadListener {
    private final Identifier biomeColormapId;
    private final Identifier optifineId;
    private BiomeColorMap colorMapping;

    private static final String OPTIFINE_PATH = "optifine/%s.properties";


    public BiomeColorMappingResourceReloadListener(Identifier id) {
        this.biomeColormapId =new Identifier(id.getNamespace(), id.getPath() + ".json");
        this.optifineId = new Identifier("minecraft", String.format(OPTIFINE_PATH, optifineId.getPath()));
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
    public void reload(ResourceManager maanger){
        PropertyImage
    }
}

package vanadium.customcolors.mapping;

import com.google.common.collect.Maps;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import vanadium.Vanadium;

import java.util.Map;

public final class LightMappings {
    private LightMappings() {}

    private static final Map<ResourceLocation, Lightmap> lightMaps = Maps.newHashMap();

    public static Lightmap get(ClientLevel world) {
        return lightMaps.get(Vanadium.getDimensionId(world));
    }

    public static void addLightMap(ResourceLocation identifier, Lightmap lightmap) {
        lightMaps.put(identifier, lightmap);
    }

    public static void clearLightMaps() {
        lightMaps.clear();
    }
}

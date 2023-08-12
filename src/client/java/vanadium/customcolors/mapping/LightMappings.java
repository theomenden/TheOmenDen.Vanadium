package vanadium.customcolors.mapping;

import com.google.common.collect.Maps;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import vanadium.VanadiumClient;

import java.util.Map;

public final class LightMappings {
    private LightMappings() {}

    private static final Map<Identifier, Lightmap> lightMaps = Maps.newHashMap();

    public static Lightmap get(ClientWorld world) {
        return lightMaps.get(VanadiumClient.getDimensionid(world));
    }

    public static void addLightMap(Identifier identifier, Lightmap lightmap) {
        lightMaps.put(identifier, lightmap);
    }

    public static void clearLightMaps() {
        lightMaps.clear();
    }
}

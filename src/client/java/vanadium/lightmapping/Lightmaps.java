package vanadium.lightmapping;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vanadium.Vanadium;

import java.util.HashMap;
import java.util.Map;

public record Lightmaps() {
    private static final Map<Identifier, Lightmap> _lightmaps = new HashMap<>();

    public static Lightmap get(World world) {
        return _lightmaps.get(Vanadium.getDimensionId(world));
    }

    public static void addLightMap(Identifier identifier, Lightmap lightmap) {
        _lightmaps.put(identifier, lightmap);
    }

    public static void clearLightmaps() {
        _lightmaps.clear();
    }
}

package vanadium.lightmapping;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import vanadium.entry.Vanadium;

import java.util.HashMap;
import java.util.Map;

public record Lightmaps() {
    private static final Map<ResourceLocation, Lightmap> lightmaps = new HashMap<>();

    public static Lightmap get(Level world) {
        return lightmaps.get(Vanadium.getDimensionId(world));
    }

    public static void addLightMap(ResourceLocation identifier, Lightmap lightmap) {
        lightmaps.put(identifier, lightmap);
    }

    public static void clearLightmaps() {
        lightmaps.clear();
    }
}

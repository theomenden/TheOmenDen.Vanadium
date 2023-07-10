package vanadium.caching;

import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import vanadium.blending.ChunkBlender;
import vanadium.enums.BiomeColorTypes;
import vanadium.models.Coordinates;

public final class ColorChunkCaching {
    private static final long Maximum30Bit = 0x03FFFFFFL;
    public static final int INVALID_CHUNK_KEY = -1;

    public static long getCachedChunkKey(Coordinates coordinates, BiomeColorTypes colorType) {
        return ((coordinates.z() & Maximum30Bit) << 26) |
        ((coordinates.x() & Maximum30Bit) << 26) |
        ((long)(coordinates.y() & 0x1F) << 52) |
                ((long) colorType.ordinal() << 57);
    }

    public static ChunkBlender getBlendedChunk(World world, ColorResolver resolver, BiomeColorTypes colorType, Coordinates coordinates, BlendCache blendCache, ColorCache colorCache, BiomeCache biomeCache) {
        ChunkBlender cachedChunk = blendCache.getChunk(coordinates, colorType);

        if(cachedChunk == null) {
            cachedChunk = blendCache.newChunk(coordinates, colorType);

            ColorBlending.generateChunkWithBlendedColors(
                    world,
                    resolver,
                    colorType,
                    coordinates,
                    cachedChunk,
                    colorCache,
                    biomeCache,
                    cachedChunk.data
            );

            cachedChunk = blendCache.putChunk(cachedChunk);
        }

        return cachedChunk;
    }
}

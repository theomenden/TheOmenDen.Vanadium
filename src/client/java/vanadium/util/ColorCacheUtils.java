package vanadium.util;

import vanadium.enums.BiomeColorTypes;
import vanadium.models.Coordinates;

public final class ColorCacheUtils {

    private static final int PRESERVED_30_BITS = 0x03FFFFFF;
    public static final int INVALID_CHUNK_KEY = -1;

    public static int getArrayIndex(int dimension, int x, int y, int z) {
        return x + dimension * (y + (z * dimension));
    }

    public static int getArrayIndex(int dimension, Coordinates coordinates) {
        return getArrayIndex(dimension, coordinates.x(), coordinates.y(), coordinates.z());
    }

    public static long getChunkKey(int chunkX, int chunkY, int chunkZ, int colorType) {
        return ((long)(chunkX & PRESERVED_30_BITS)) |
               ((long)(chunkZ & PRESERVED_30_BITS) << 26) |
               ((long)(chunkY & 0x1F) << 52) |
               ((long)(colorType << 57));
    }

    public static long getChunkKey(Coordinates coordinates, int colorType) {
        return getChunkKey(coordinates.x(), coordinates.y(), coordinates.z(), colorType);
    }
}

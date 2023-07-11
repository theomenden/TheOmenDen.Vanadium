package vanadium.caching;

import vanadium.enums.BiomeColorTypes;
import vanadium.models.Coordinates;

public final class ColorBlendingCache {
    private static final int representationSize = 0x03FFFFFF;
    public static final int INVALID_CHUNK_KEY = -1;

    public static int getArrayIndex(int dimension, Coordinates coordinates) {
        return coordinates.x() + coordinates.y() * dimension + coordinates.z() * (int)Math.pow(dimension, 2);
    }

    public static int getArrayIndex(int dimension, int x, int y, int z) {
        return x + y * dimension + z * (int)Math.pow(dimension, 2);
    }

    public static long getChunkCacheKey(Coordinates coordinates, BiomeColorTypes colorType) {
        return ((long)(coordinates.x() & representationSize)) |
                ((long)(coordinates.z() & representationSize) << 26) |
                ((long)(coordinates.y() & 0x1F) << 52) |
                ((long)colorType.ordinal() << 57);
    }
}

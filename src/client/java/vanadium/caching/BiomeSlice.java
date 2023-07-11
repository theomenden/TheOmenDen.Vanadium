package vanadium.caching;

import net.minecraft.world.biome.Biome;
import vanadium.models.Coordinates;

import java.util.Arrays;
import java.util.stream.IntStream;

public final class BiomeSlice extends BaseCacheStrategy {
    public Biome[] data;

    public BiomeSlice() {
        super();
        this.data = new Biome[64];
    }

    public void invalidateCacheData() {
        Arrays.fill(this.data, null);
    }

    @Override
    public void invalidateRegionCacheData(Coordinates minimumCoordinates, Coordinates maximumCoordinates) {
        int cachingDimensions = 4;

        Coordinates toAdjust = new Coordinates(0, 0, 0);

        IntStream.range(minimumCoordinates.y(), maximumCoordinates.y() + cachingDimensions).forEach(y1 ->
                IntStream.range(minimumCoordinates.z(), maximumCoordinates.z()).forEach(z1 ->
                        IntStream.range(minimumCoordinates.x(), maximumCoordinates.x() + cachingDimensions).forEach(x1 -> {
                            int cacheIndex = ColorBlendingCache.getArrayIndex(cachingDimensions,x1, y1, z1);
                            this.data[cacheIndex] = null;
                        })
                )
        );
        ;
    }

    @Override
    public void invalidCacheData() {
        Arrays.fill(this.data, null);
    }
}

package vanadium.biomeblending.caching;

import vanadium.biomeblending.caching.strategies.BiomeSlice;
import vanadium.biomeblending.caching.strategies.SlicedCacheStrategy;

public final class BiomeCache extends SlicedCacheStrategy<BiomeSlice> {
    public BiomeCache(int count) {
        super(count);
    }

    @Override
    public BiomeSlice createSlice(int size, int salt) {
        return new BiomeSlice(size, salt);
    }
}

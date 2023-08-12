package vanadium.biomeblending.caching;

import vanadium.biomeblending.caching.strategies.ColorSlice;
import vanadium.biomeblending.caching.strategies.SlicedCacheStrategy;

public class ColorCache extends SlicedCacheStrategy<ColorSlice> {

    public ColorCache(int count) {
        super(count);
    }

    @Override
    public ColorSlice createSlice(int sliceSize, int salt) {
        return new ColorSlice(sliceSize, salt);
    }
}
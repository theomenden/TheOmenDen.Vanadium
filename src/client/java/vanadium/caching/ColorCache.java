package vanadium.caching;

public class ColorCache extends SlicingCachingStrategy<ColorSlice>{

    public ColorCache(int count) {
        super(count);
    }

    @Override
    public ColorSlice createSlice(int sliceSize, int salt) {
        return new ColorSlice(sliceSize, salt);
    }
}

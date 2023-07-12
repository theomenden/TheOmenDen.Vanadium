package vanadium.caching;

public final class BiomeCache extends SlicingCachingStrategy<BiomeSlice> {
    public BiomeCache(int count) {super(count);}

    @Override
    public BiomeSlice createSlice(int sliceSize, int salt) {
        BiomeSlice result = new BiomeSlice(sliceSize, salt);

        return result;
    }
}

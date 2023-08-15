package vanadium.biomeblending.caching.strategies;

import java.util.Arrays;

public final class ColorSlice extends BaseSlice {
    public int[] data;

    public ColorSlice(int size, int salt) {
        super(size, salt);
        this.data = new int[(int)Math.pow(size, 3)];
    }

    @Override
    public void invalidateCacheData() {
        Arrays.fill(this.data, 0);
    }
}
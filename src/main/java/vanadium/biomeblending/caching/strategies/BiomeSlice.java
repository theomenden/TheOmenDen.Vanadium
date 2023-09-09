package vanadium.biomeblending.caching.strategies;


import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;

public final class BiomeSlice extends BaseSlice {
    public Biome[] data;

    public BiomeSlice(int size, int salt) {
        super(size, salt);
        this.data = new Biome[(int)Math.pow(size, 3)];
    }

    public void invalidateCacheData() {
        Arrays.fill(this.data, null);
    }
}
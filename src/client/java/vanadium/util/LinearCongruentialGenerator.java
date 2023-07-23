package vanadium.util;

import java.util.stream.IntStream;

public final class LinearCongruentialGenerator {
    private LinearCongruentialGenerator() {

    }

    final static int mask = (1 << 31) - 1;

    public static IntStream generateRandomNoise(int seed) {
        return IntStream.iterate(seed, s -> (int) (((long) s * 21_401_303 + 2_531_011) & Integer.MAX_VALUE))
                        .map(i -> i >>16);
    }
}

package org.game.terrain;

public class OtherNoise {
    private static int seed;

    public static void set_seed(int seed) {
        OtherNoise.seed = seed;
    }

    public static int get_white_noise(int min_bound, int max_bound, int x, int y) {
        long value = ((long) x << 32) | (y & 0xFFFFFFFFL);

        value ^= seed;

        // SplitMix64 mixing
        value = (value ^ (value >>> 30)) * 0xbf58476d1ce4e5b9L;
        value = (value ^ (value >>> 27)) * 0x94d049bb133111ebL;
        value ^= value >>> 31;

        return min_bound + (int) Long.remainderUnsigned(
                value,
                (long) max_bound - min_bound + 1
        );
    }
}

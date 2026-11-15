package org.game.globalStats;

import java.util.*;

public class FpsManager {
    private static int sampled = 0;
    private static final int SAMPLES_STORED = 1000;
    private static double samples[] = new double[SAMPLES_STORED];

    public static void update(double latest_sample) {
        samples[sampled % SAMPLES_STORED] = latest_sample;
        sampled++;

    }

    public static double get_average() {
        // Avoid division by 0
        if (sampled == 0)
            return -1;

        double total = Arrays.stream(samples).sum();

        return total / Math.min(sampled, SAMPLES_STORED);
    }

    public static double get_1_percent_low() {
        int count = Math.min(sampled, SAMPLES_STORED);

        if (count < 100)
            return -1;

        double[] sorted = Arrays.copyOf(samples, count);
        Arrays.sort(sorted);

        int end_index = Math.max(1, count / 100);

        double total = 0;

        for (int i = 0; i < end_index; i++) {
            total += sorted[i];
        }

        return total / end_index;
    }
}

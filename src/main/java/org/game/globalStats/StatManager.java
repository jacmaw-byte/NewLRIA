package org.game.globalStats;

import java.util.*;

public class StatManager<T extends Number> {

    private static final int SAMPLES_STORED = 1000;

    private final ArrayList<T> samples = new ArrayList<>(SAMPLES_STORED);
    private int sampled = 0;

    public void update(T latestSample) {
        if (samples.size() < SAMPLES_STORED) {
            samples.add(latestSample);
        } else {
            samples.set(sampled % SAMPLES_STORED, latestSample);
        }

        sampled++;
    }

    public Double get_average() {
        if (samples.isEmpty()) {
            return null;
        }

        double total = 0;

        for (T sample : samples) {
            total += sample.doubleValue();
        }

        return total / samples.size();
    }

    public Double get_1_percent_low() {
        int count = samples.size();

        if (count < 100) {
            return null;
        }

        // Copy the samples so we don't modify the original rolling buffer
        ArrayList<T> sorted = new ArrayList<>(samples);

        sorted.sort(Comparator.comparingDouble(Number::doubleValue));

        // Lowest 1% of samples
        int endIndex = Math.max(1, count / 100);

        double total = 0;

        for (int i = 0; i < endIndex; i++) {
            total += sorted.get(i).doubleValue();
        }

        return total / endIndex;
    }

    public int get_sample_count() {
        return samples.size();
    }
}
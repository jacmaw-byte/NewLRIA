package org.gameManager.terrain;

import org.gameManager.worldGen.FastNoiseLite;

public class NoiseManager {

    private FastNoiseLite terrain_noise;
    private FastNoiseLite stone_noise;
    private FastNoiseLite ore_noise;

    private int seed = 1337;

    public FastNoiseLite get_terrain_noise(){ return terrain_noise; }
    public FastNoiseLite get_stone_noise(){ return stone_noise; }
    public FastNoiseLite get_ore_noise(){ return ore_noise; }
    public int get_seed() { return seed; }

    public NoiseManager(){
        init_terrain_noise();
        init_stone_noise();
        init_ore_noise();
    }

    public boolean is_iron(int world_x, int world_y){
        float depth = -world_y;

        float ore_value = ore_noise.GetNoise(world_x, world_y);

        float base_threshold = 0.6f;
        float depth_scaling = 0.0008f; // Adjust this to control how fast spawn rates increase
        float threshold = base_threshold - (depth * depth_scaling);

        float min_threshold = 0.3f;
        threshold = Math.max(min_threshold, threshold);

        return ore_value > threshold;
    }

    private void init_terrain_noise(){
        terrain_noise = new FastNoiseLite();

        terrain_noise.SetSeed(seed ^ 450391301);
        terrain_noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        terrain_noise.SetFrequency(0.003f);
        terrain_noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        terrain_noise.SetFractalOctaves(6);
    }

    private void init_stone_noise(){
        stone_noise = new FastNoiseLite();

        stone_noise.SetSeed(seed ^ 595278487);
        stone_noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        stone_noise.SetFrequency(0.003f);
        stone_noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        stone_noise.SetFractalOctaves(1);
    }

    private void init_ore_noise(){
        ore_noise = new FastNoiseLite();

        ore_noise.SetSeed(seed);
        ore_noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        ore_noise.SetFrequency(0.003f);
        ore_noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        ore_noise.SetFractalOctaves(4);
    }
}

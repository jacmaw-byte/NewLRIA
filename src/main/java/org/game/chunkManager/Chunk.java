package org.game.chunkManager;

import org.game.cell.Cell;
import org.game.cell.Cells;
import org.game.terrain.NoiseManager;

import static org.Constants.*;
import org.joml.Vector2i;

public class Chunk {
    private final static int GRASS_HEIGHT_VARIANCE = 1;
    private final static int GRASS_DEPTH = 4;
    private final static float STONE_CHANCE_FACTOR = 0.1f;

    private final static float IRON_ORE_THRESHOLD = 0.99f;

    private final Cell[] cells = new Cell[CHUNK_SIZE * CHUNK_SIZE];

    private final Vector2i chunk_position;
    private Vector2i world_position;

    private final NoiseManager noise_manager;
    private final ChunkManager manager;

    public Vector2i get_world_position(){
        return world_position;
    }

    public Vector2i get_chunk_position(){
        return chunk_position;
    }

    public Cell get_cell(int x, int y){
        int index = x * CHUNK_SIZE + y;

        if (index < 0 || index > CHUNK_SIZE * CHUNK_SIZE)
            return null;

        return cells[index];
    }

    public void set_cell(int x, int y, Cell cell){
        cells[x * CHUNK_SIZE + y] = cell;
    }

    public void generate_data(){

        for (int x = 0; x < CHUNK_SIZE; x++) {
            int world_x = x + world_position.x;
            int height = (int)Math.floor(noise_manager.get_terrain_noise().GetNoise((world_x) * 0.2f, 1) * 70.0f);

            int grass_height = height - (GRASS_DEPTH + (int)(Math.random() * (((GRASS_DEPTH + GRASS_HEIGHT_VARIANCE) - GRASS_DEPTH) + 1)));

            for (int y = 0; y < CHUNK_SIZE; y++) {
                int world_y = y + world_position.y;

                boolean is_stone = true;
                boolean is_iron = noise_manager.is_iron(world_x, world_y);

                if (world_y > height - 200 && !is_iron){
                    double stone_noise_value = noise_manager.get_stone_noise().GetNoise(world_x, world_y) * 20.0f;

                    float stone_spawn_chance = (float)world_y / 3.0f + 30.0f;
                    is_stone = stone_noise_value > stone_spawn_chance;
                }

                // Determine the cell type based on position
                if (world_y <= height && world_y >= grass_height){
                    set_cell(x, y, new Cell(Cells.GRASS));
                }
                else if (world_y < height && is_iron){
                    set_cell(x, y, new Cell(Cells.IRON));
                }
                else if (world_y < height && is_stone){
                    set_cell(x, y, new Cell(Cells.STONE));
                }
                else if (world_y < height){
                    set_cell(x, y, new Cell(Cells.DIRT));
                }
                else{
                    set_cell(x, y, new Cell(Cells.AIR));
                }
            }
        }
    }

    public Chunk(Vector2i chunk_position, ChunkManager manager){
        this.chunk_position = chunk_position;
        world_position = new Vector2i();

        chunk_position.mul(CHUNK_SIZE, world_position);

        noise_manager = manager.get_server().get_noise_manager();
        this.manager = manager;

        generate_data();
    }
}

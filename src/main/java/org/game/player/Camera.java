package org.game.player;

import org.game.chunkManager.Chunk;

import org.game.chunkManager.ChunkManager;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

import static org.Constants.*;

public class Camera {
    private Vector2f position;
    public List<Chunk> chunks_in_view;
    private ChunkManager chunk_manager;

    public Camera(){
        chunks_in_view = new ArrayList<>();
    }

    public void set_chunk_manager(ChunkManager chunk_manager){
        this.chunk_manager = chunk_manager;
    }

    public Vector2f get_position(){
        return position;
    }

    public void set_position(Vector2f position){
        this.position = position;
    }

    public void set_chunks_in_view(){
        Vector2i camera_chunk_pos = get_chunk_position();

        int chunk_radius_x = (int)Math.ceil((float)GRID_WIDTH / (float)(2 * CHUNK_SIZE));
        int chunk_radius_y = (int)Math.ceil((float)GRID_HEIGHT / (float)(2 * CHUNK_SIZE));

        int start_x = camera_chunk_pos.x - chunk_radius_x;
        int start_y = camera_chunk_pos.y - chunk_radius_y;

        int end_x = camera_chunk_pos.x + chunk_radius_x + 1;
        int end_y = camera_chunk_pos.y + chunk_radius_y + 1;

        for (int x = start_x; x < end_x; x++) {
            for (int y = start_y; y < end_y; y++) {
                Vector2i chunk_pos = new Vector2i(x, y);

                if (chunk_manager.chunks.containsKey(chunk_pos)){
                    Chunk chunk = chunk_manager.get_chunk(chunk_pos);

                    chunks_in_view.add(chunk);
                }
            }
        }
    }

    public void clear_chunks_in_view(){
        chunks_in_view.clear();
    }

    public Vector2i get_chunk_position(){
        return new Vector2i((int)Math.floor(position.x / CHUNK_SIZE), (int)Math.floor(position.y / CHUNK_SIZE));
    }
}

package org.gameManager.player;

import org.gameManager.GameManager;
import org.gameManager.Time;
import org.gameManager.chunkManager.Chunk;

import org.gameManager.chunkManager.ChunkManager;
import org.gameManager.renderManager.RenderManager;
import org.types.Vector2I;
import org.types.Vector2f;

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
        Vector2I camera_chunk_pos = get_chunk_position();

        int chunk_radius_x = (int)Math.ceil((float)GRID_WIDTH / (float)(2 * CHUNK_SIZE));
        int chunk_radius_y = (int)Math.ceil((float)GRID_HEIGHT / (float)(2 * CHUNK_SIZE));

        int start_x = camera_chunk_pos.x - chunk_radius_x;
        int start_y = camera_chunk_pos.y - chunk_radius_y;

        int end_x = camera_chunk_pos.x + chunk_radius_x + 1;
        int end_y = camera_chunk_pos.y + chunk_radius_y + 1;

        for (int x = start_x; x < end_x; x++) {
            for (int y = start_y; y < end_y; y++) {
                Vector2I chunk_pos = new Vector2I(x, y);

                if (chunk_manager.chunks.containsKey(chunk_pos)){
                    chunks_in_view.add(chunk_manager.get_chunk(chunk_pos));
                }
            }
        }
    }

    public void clear_chunks_in_view(){
        chunks_in_view.clear();
    }

    public Vector2I get_chunk_position(){
        return position.scale(1.0f / (float)CHUNK_SIZE).floor();
    }
}

package org.gameManager.chunkManager;

import org.gameManager.GameManager;
import org.gameManager.cell.Cell;
import org.gameManager.player.Player;
import org.gameManager.renderManager.RenderManager;
import org.types.Vector2I;

import java.util.HashMap;
import java.util.Map;
import static org.Constants.*;

public class ChunkManager {
    public static Vector2I render_distance_dimensions;

    public HashMap<Vector2I, Chunk> chunks;

    public Chunk get_chunk(Vector2I position){
        return chunks.get(position);
    }

    private Player player;

    public ChunkManager(){
        chunks = new HashMap<>();
        render_distance_dimensions = new Vector2I(
                (int)Math.ceil(GRID_WIDTH / (float)CHUNK_SIZE),
                (int)Math.ceil(GRID_HEIGHT / (float)CHUNK_SIZE)
        );
    }

    public void set_player(Player player){
        this.player = player;
    }

    private static final int MAX_CHUNK_A_FRAME = 1;

    public void generate_chunks(){
        Vector2I player_chunk_position = player.get_position().scale(1.0f / CHUNK_SIZE).floor();

        int chunk_generated_this_frame = 0;
        for (int x = -render_distance_dimensions.x; x < render_distance_dimensions.x + 1; x++) {
            for (int y = -render_distance_dimensions.y; y < render_distance_dimensions.y; y++) {
                Vector2I chunk_pos = player_chunk_position.add(new Vector2I(x, y));

                // Don't generate chunks that exist
                if (chunks.containsKey(chunk_pos)){
                    continue;
                }
                Chunk chunk = new Chunk(chunk_pos);

                chunks.put(chunk_pos, chunk);

                if (chunk_generated_this_frame == MAX_CHUNK_A_FRAME)
                    // Throttle chunks to prevent lag
                    break;

                chunk_generated_this_frame++;
            }
        }
    }

    public Cell get_cell(int x, int y){
        int chunk_x = (int)Math.floor((float)x / CHUNK_SIZE);
        int chunk_y = (int)Math.floor((float)y / CHUNK_SIZE);

        Chunk chunk = chunks.get(new Vector2I(chunk_x, chunk_y));

        if (chunk == null)
            return null;

        int local_x = x - chunk.get_world_position().x;
        int local_y = y - chunk.get_world_position().y;

        return chunk.get_cell(local_x, local_y);
    }
}

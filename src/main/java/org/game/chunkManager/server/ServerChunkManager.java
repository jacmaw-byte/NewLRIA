package org.game.chunkManager.server;

import org.game.cell.Cell;
import org.game.cell.CellID;
import org.game.server.server.HostServer;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.HashMap;

import static org.Constants.*;

public class ServerChunkManager {
    public static Vector2i render_distance_dimensions;

    public HashMap<Vector2i, ServerChunk> chunks;

    private HostServer server;

    public ServerChunk get_chunk(Vector2i position){
        return chunks.get(position);
    }
    public HostServer get_server() { return server; }

    public ServerChunkManager(HostServer server){
        chunks = new HashMap<>();
        render_distance_dimensions = new Vector2i(
                (int)Math.ceil(GRID_WIDTH / (float)CHUNK_SIZE),
                (int)Math.ceil(GRID_HEIGHT / (float)CHUNK_SIZE)
        );

        this.server = server;
    }

    private static final int MAX_CHUNK_A_FRAME = 1;

    public void generate_chunks_around_player(Vector2f camera_position){
        int player_chunk_x = (int)Math.floor(camera_position.x / CHUNK_SIZE);
        int player_chunk_y = (int)Math.floor(camera_position.y / CHUNK_SIZE);

        int chunk_generated_this_frame = 0;
        for (int x = -render_distance_dimensions.x; x < render_distance_dimensions.x + 1; x++) {
            for (int y = -render_distance_dimensions.y; y < render_distance_dimensions.y; y++) {
                Vector2i chunk_pos = new Vector2i(
                        player_chunk_x + x,
                        player_chunk_y + y
                );

                // Don't generate chunks that exist
                if (chunks.containsKey(chunk_pos)){
                    continue;
                }
                ServerChunk chunk = new ServerChunk(chunk_pos, this);

                chunks.put(chunk_pos, chunk);

                if (chunk_generated_this_frame == MAX_CHUNK_A_FRAME)
                    // Throttle chunks to prevent lag
                    return;

                chunk_generated_this_frame++;
            }
        }
    }

    public ServerChunk request_chunk(Vector2i position){

        if (chunks.containsKey(position)){
            return chunks.get(position);
        }
        else {
            ServerChunk chunk = new ServerChunk(position, this);

            chunks.put(position, chunk);

            return chunk;
        }
    }

    public CellID get_cell(int x, int y){
        int chunk_x = (int)Math.floor((float)x / CHUNK_SIZE);
        int chunk_y = (int)Math.floor((float)y / CHUNK_SIZE);

        ServerChunk chunk = chunks.get(new Vector2i(chunk_x, chunk_y));

        if (chunk == null)
            return null;

        int local_x = x - chunk.get_world_position().x;
        int local_y = y - chunk.get_world_position().y;

        return chunk.get_cell(local_x, local_y);
    }
}

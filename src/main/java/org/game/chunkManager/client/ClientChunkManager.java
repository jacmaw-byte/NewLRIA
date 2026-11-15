package org.game.chunkManager.client;

import org.game.cell.Cell;
import org.game.chunkManager.server.ChunkData;
import org.game.server.client.Client;
import org.joml.Vector2i;

import java.util.HashMap;

import static org.Constants.CHUNK_SIZE;

public class ClientChunkManager {
    private Client client;

    private int chunks_processed_this_frame = 0;
    private static final int CHUNK_THROTTLE = 1;

    private HashMap<Vector2i, ClientChunk> chunk_map = new HashMap<>();

    public ClientChunkManager(Client client){
        this.client = client;
    }

    public ClientChunk request_chunk(Vector2i position){
        if (chunk_map.containsKey(position)){
            return chunk_map.get(position);
        }
        else{
            client.get_server().request_chunk(client.get_id(), position);
            return null;
        }
    }

    public void receive_chunk(ChunkData data){
        if (chunks_processed_this_frame < CHUNK_THROTTLE){
            chunk_map.put(data.position(), new ClientChunk(this, data));
            chunks_processed_this_frame++;
        }
    }

    public Cell get_cell(int x, int y){
        int chunk_x = (int)Math.floor((float)x / CHUNK_SIZE);
        int chunk_y = (int)Math.floor((float)y / CHUNK_SIZE);

        ClientChunk chunk = chunk_map.get(new Vector2i(chunk_x, chunk_y));

        if (chunk == null)
            return null;

        int local_x = x - chunk.get_world_position().x;
        int local_y = y - chunk.get_world_position().y;

        return chunk.get_cell(local_x, local_y);
    }

    public void reset_chunk_throttle() {
        chunks_processed_this_frame = 0;
    }
}

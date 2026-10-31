package org.game.server.client;

import org.game.cell.Cell;
import org.game.chunkManager.Chunk;
import org.game.server.server.Server;
import org.joml.Vector2i;

import java.util.HashMap;

import static org.Constants.CHUNK_SIZE;

public class ClientChunkManager {
    private Client client;

    private HashMap<Vector2i, Chunk> chunk_map = new HashMap<>();

    public ClientChunkManager(Client client){
        this.client = client;
    }

    public Chunk request_chunk(Vector2i position){
        if (chunk_map.containsKey(position)){
            return chunk_map.get(position);
        }
        else{
            client.get_server().request_chunk(client.get_id(), position);
            return null;
        }
    }

    public void receive_chunk(Chunk chunk){
        chunk_map.put(chunk.get_chunk_position(), chunk);
    }

    public Cell get_cell(int x, int y){
        int chunk_x = (int)Math.floor((float)x / CHUNK_SIZE);
        int chunk_y = (int)Math.floor((float)y / CHUNK_SIZE);

        Chunk chunk = chunk_map.get(new Vector2i(chunk_x, chunk_y));

        if (chunk == null)
            return null;

        int local_x = x - chunk.get_world_position().x;
        int local_y = y - chunk.get_world_position().y;

        return chunk.get_cell(local_x, local_y);
    }
}

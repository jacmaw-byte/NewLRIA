package org.game.chunkManager.client;

import org.game.cell.Cell;
import org.game.chunkManager.server.ChunkData;
import org.game.player.client.ClientPlayer;
import org.game.server.client.Client;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.Constants.CHUNK_SIZE;

public class ClientChunkManager {
    private Client client;

    private int chunks_processed_this_frame = 0;
    private static final int CHUNK_THROTTLE = 4;

    private final Set<Vector2i> requested = new HashSet<>();

    private HashMap<Vector2i, ClientChunk> chunk_map = new HashMap<>();

    public ClientChunkManager(Client client){
        this.client = client;
    }

    public ClientChunk request_chunk(Vector2i position){
        ClientChunk existing = chunk_map.get(position);
        if (existing != null) return existing;

        Vector2i key = new Vector2i(position);
        if (requested.add(key)) {
            client.get_server().request_chunk(client.get_id(), key);
        }
        return null;
    }

    public boolean can_accept_chunk() {
        return chunks_processed_this_frame < CHUNK_THROTTLE;
    }

    public void get_buffer_chunks() {
        Vector2i camera_pos = client.get_player().get_camera().get_chunk_position();
        for (int x = -4; x < 4; x++) {
            for (int y = -3; y < 3; y++) {
                request_chunk(new Vector2i(x + camera_pos.x, y + camera_pos.y));
            }
        }
    }

    public void receive_chunk(ChunkData data){
        Vector2i key = new Vector2i(data.position());
        requested.remove(key);

        // ignore duplicate replies
        if (chunk_map.containsKey(key)) return;

        chunk_map.put(key, new ClientChunk(this, data));
        chunks_processed_this_frame++;
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

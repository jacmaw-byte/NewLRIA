package org.game.chunkManager.client;

import org.game.cell.Cell;
import org.game.cell.CellID;
import org.game.cell.CellType;
import org.game.cell.Cells;
import org.game.chunkManager.server.ChunkData;
import org.game.chunkManager.server.ServerChunkManager;
import org.game.terrain.NoiseManager;
import org.joml.Vector2i;

import static org.Constants.CHUNK_SIZE;

public class ClientChunk {
    private final static int GRASS_HEIGHT_VARIANCE = 1;
    private final static int GRASS_DEPTH = 4;
    private final static float STONE_CHANCE_FACTOR = 0.1f;

    private final static float IRON_ORE_THRESHOLD = 0.99f;

    private final Cell[] cells = new Cell[CHUNK_SIZE * CHUNK_SIZE];

    private final Vector2i chunk_position;
    private Vector2i world_position;

    private final ClientChunkManager manager;

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

    public ClientChunk(ClientChunkManager manager, ChunkData chunk_data) {
        this.chunk_position = chunk_data.position();
        world_position = new Vector2i();

        chunk_data.position().mul(CHUNK_SIZE, world_position);
        this.manager = manager;

        for (int i = 0; i < chunk_data.cells().length; i++) {
            CellType type = Cells.get(chunk_data.cells()[i]);

            int x = i % CHUNK_SIZE;
            int y = i / CHUNK_SIZE;

            cells[i] = new Cell(type, x, y);
        }
    }
}

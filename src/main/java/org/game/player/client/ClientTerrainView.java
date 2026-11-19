package org.game.player.client;

import org.game.cell.Cell;
import org.game.chunkManager.client.ClientChunkManager;

public final class ClientTerrainView implements TerrainView {
    private final ClientChunkManager cm;
    public ClientTerrainView(ClientChunkManager cm) { this.cm = cm; }

    @Override public boolean is_solid(int x, int y) {
        Cell c = cm.get_cell(x, y);
        return c != null && c.get_type().collides();
    }
    @Override public boolean is_loaded(int x, int y) { return cm.get_cell(x, y) != null; }
}
package org.game.player.server;

import org.game.cell.Cell;
import org.game.cell.CellID;
import org.game.cell.Cells;
import org.game.chunkManager.server.ServerChunkManager;
import org.game.player.client.TerrainView;

public final class ServerTerrainView implements TerrainView {
    private final ServerChunkManager cm;
    public ServerTerrainView(ServerChunkManager cm) { this.cm = cm; }

    @Override public boolean is_solid(int x, int y) {
        CellID c = cm.get_cell(x, y);          // adapt to your server API
        return c != null && Cells.get(c).collides();
    }
    @Override public boolean is_loaded(int x, int y) { return cm.get_cell(x, y) != null; }
}
package org.game.chunkManager.server;

import org.game.cell.CellID;
import org.joml.Vector2i;

public record ChunkData(
        CellID[] cells,
        Vector2i position
) {
}

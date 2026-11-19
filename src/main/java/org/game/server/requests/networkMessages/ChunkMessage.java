package org.game.server.requests.networkMessages;

import org.game.chunkManager.server.ChunkData;

public record ChunkMessage(
        ChunkData chunk_data
) implements NetworkMessage {
}

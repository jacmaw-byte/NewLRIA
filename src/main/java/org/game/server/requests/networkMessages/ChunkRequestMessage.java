package org.game.server.requests.networkMessages;

import org.game.server.requests.ChunkRequest;

public record ChunkRequestMessage(
        ChunkRequest request
) implements NetworkMessage {
}

package org.game.server.requests.networkMessages;

import org.game.server.requests.PlayerDataRequest;

public record PlayerDataRequestMessage(
        PlayerDataRequest request
) implements NetworkMessage {
}

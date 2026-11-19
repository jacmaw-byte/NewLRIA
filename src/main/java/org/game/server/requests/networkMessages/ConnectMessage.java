package org.game.server.requests.networkMessages;

public record ConnectMessage(
        int id
) implements NetworkMessage {
}

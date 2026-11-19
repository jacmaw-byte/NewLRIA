package org.game.server.requests.networkMessages;

import org.game.player.common.PlayerInput;

public record PlayerInputMessage(
        int id,
        PlayerInput input
) implements NetworkMessage {
}

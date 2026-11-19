package org.game.player.common;
import org.joml.Vector2f;

import java.io.Serializable;
import java.util.UUID;

public record MultiplayerPlayer(
        String name,
        UUID uuid,
        Vector2f position,
        boolean flipped
) implements Serializable {
}

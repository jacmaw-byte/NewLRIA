package org.game.player.common;

import org.joml.Vector2f;

public class MultiplayerPlayerClientSide{
    public MultiplayerPlayer player;
    public Vector2f last_position;
    public double time_since_update;

    public MultiplayerPlayerClientSide(MultiplayerPlayer player) {
        this.player = player;
        this.last_position = player.position();
        time_since_update = 0.0;
    }
}

package org.game.player.common;

import java.io.Serializable;

public record PlayerData(
        float x,
        float y,
        float velocity_x,
        float velocity_y,
        float health,
        float hunger,
        boolean sprinting,
        boolean grounded,
        boolean flipped
) implements Serializable {
    public PlayerData with_state(float x, float y, float vx, float vy, boolean grounded, boolean flipped) {
        return new PlayerData(x, y, vx, vy, /* the two 20s */ health, 100, sprinting, grounded, flipped);
    }
}
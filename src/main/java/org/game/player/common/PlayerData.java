package org.game.player.common;

public record PlayerData(
        float x,
        float y,
        float velocityX,
        float velocityY,
        float health,
        float hunger,
        boolean sprinting,
        boolean grounded
) {}
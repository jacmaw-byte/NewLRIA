package org.game.player.common;

public record PlayerInput(
        boolean left,
        boolean right,
        boolean jump,
        boolean up,
        boolean down,
        boolean sprint
) {}
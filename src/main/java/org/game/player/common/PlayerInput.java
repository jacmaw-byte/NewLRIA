package org.game.player.common;

import java.io.Serializable;

public record PlayerInput(
        boolean left,
        boolean right,
        boolean jump,
        boolean up,
        boolean down,
        boolean sprint
) implements Serializable {}
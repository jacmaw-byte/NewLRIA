package org.gameManager.cell;

import org.types.Colour;

public record CellType(
        CellID id,
        Colour colour,
        boolean collides,
        int colour_noise_amp
) {
}

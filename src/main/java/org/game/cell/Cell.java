package org.game.cell;

import org.game.scheisseRenderer.Texture;
import org.types.Colour;

public class Cell {
    private CellType type;
    private Colour colour;

    public Cell(CellType type){
        set_type(type);
    }

    public CellType get_type(){
        return type;
    }

    public Colour get_colour(){
        return colour;
    }

    public void set_type(CellType type){
        this.type = type;

        if (type.colour_noise_amp() == 0){
            this.colour = new Colour(
                    type.colour().r(),
                    type.colour().g(),
                    type.colour().b()
            );
        }
        else{
            // This normalises the value between -1 to 1 and
            // multiplies by the type's noise amplification
            int colour_deviation = (int)((Math.random() * 2 - 1) * type.colour_noise_amp());

            this.colour = new Colour(
                    type.colour().r() + colour_deviation,
                    type.colour().g() + colour_deviation,
                    type.colour().b() + colour_deviation
            );
        }
    }

    public static Texture get_cell_texture(CellType cell_type){
        Texture texture = new Texture(10, 10, 3);

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Colour colour = new Cell(cell_type).get_colour();

                texture.set_pixel(x, y, colour);
            }
        }

        return texture;
    }
}

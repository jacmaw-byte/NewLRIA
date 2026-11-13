package org.game.scheisseRenderer.UI;

import org.game.scheisseRenderer.Texture;
import org.game.scheisseRenderer.image.Sprite;
import org.joml.Vector2f;
import org.types.ColourA;

public class UI_Square extends UI_Element {
    private Sprite sprite;

    public UI_Square(String name, Vector2f position, Vector2f dimensions, ColourA colour) {
        set_position(position);
        this.name = name;
        this.dimensions = dimensions;

        Texture texture = new Texture(1, 1, 3);
        texture.set_pixel(0, 0, colour);

        sprite = new Sprite(texture, new Vector2f(), dimensions);
    }

    @Override
    public void draw() {
        sprite.get_transform().set_position(get_screen_position());

        sprite.draw();
    }
}

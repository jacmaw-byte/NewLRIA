package org.game.scheisseRenderer.UI;

import org.game.scheisseRenderer.image.Sprite;
import org.game.scheisseRenderer.image.SpriteAtlas;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class UI_Image extends UI_Element {
    private Sprite sprite;

    public UI_Image(String name, String path, Vector2f position, Vector2f dimensions){
        set_position(position);
        this.name = name;
        this.dimensions = dimensions;
        sprite = new Sprite(path, position, dimensions);
    }

    public UI_Image(String name, Vector2f position, Vector2f dimensions, SpriteAtlas atlas, Vector2i atlas_position){
        set_position(position);
        this.name = name;
        this.dimensions = dimensions;
        sprite = new Sprite(position, dimensions, atlas, atlas_position);
    }

    @Override
    public void draw() {
        sprite.get_transform().set_position(get_screen_position());
        sprite.draw();
    }
}

package org.game.renderManager.UI;

import org.game.renderManager.image.Sprite;
import org.joml.Vector2f;

public class UI_Image extends UI_Element {
    private Sprite sprite;

    public UI_Image(String path, Vector2f position, Vector2f dimensions){
        sprite = new Sprite(path, position, dimensions);
    }

    @Override
    public void draw() {
        sprite.draw();
    }

    @Override
    public void update() {

    }
}

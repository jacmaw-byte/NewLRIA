package org.game.scheisseRenderer.UI;

import org.game.input.Input;
import org.game.scheisseRenderer.image.Sprite;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class UI_Button extends UI_Element {
    private ButtonCallback callback;
    private String text;
    // Child references
    private UI_TextLabel text_label;
    private UI_Image image;

    public UI_Button(String name, Vector2f position, Vector2f size, String text, ButtonCallback callback){
        set_position(position);
        this.dimensions = size;
        this.callback = callback;
        this.text = text;
        this.name = name;

        image = new UI_Image("sprite", new Vector2f(), size, UI.ui_atlas, new Vector2i(2, 0));

        text_label = new UI_TextLabel("label", new Vector2f(), (int)size.y, text);

        text_label.centralise(dimensions);
        
        add_child(image);
        add_child(text_label);
    }

    @Override
    public boolean clickable() {
        return true;
    }

    @Override
    public void update() {
        boolean mouse_hovering = is_hovering();

        if (mouse_hovering && Input.get_mouse_pressed(0)){
            callback.callback();
        }
    }
}

package org.game.scheisseRenderer.UI;

import org.game.input.Input;
import org.game.input.InputCondition;
import org.game.scheisseRenderer.UI.containers.OutputContainer;
import org.game.scheisseRenderer.UI.containers.OutputTypes;
import org.game.scheisseRenderer.image.Sprite;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.types.ColourA;

import static org.lwjgl.glfw.GLFW.*;

public class UI_InputField<T extends OutputTypes> extends UI_Element {
    private OutputContainer<T> output;
    private UI_TextLabel text;
    private UI_Image background;
    private UI_Square text_cursor;

    private int padding;
    private boolean selected;

    public UI_InputField(
            String name,
            Vector2f position,
            Vector2f dimensions,
            String starting_text,
            OutputContainer<T> container,
            int padding
    ){
        set_position(position);
        this.dimensions = dimensions;
        this.name = name;
        this.padding = padding;

        background = new UI_Image(
                "sprite",
                new Vector2f(),
                dimensions,
                UI.ui_atlas,
                new Vector2i(2, 0)
        );

        text_cursor = new UI_Square(
                "cursor",
                new Vector2f(0, 5),
                new Vector2f(10, dimensions.y - 10),
                new ColourA(255, 255, 255, 255)
        );

        output = container;
        output.set_data_from_string(starting_text);

        selected = false;

        int font_height = (int)dimensions.y;

        text = new UI_TextLabel(
                "input label",
                new Vector2f(0, dimensions.y / 4),
                font_height,
                starting_text
        );

        add_child(background);
        add_child(text);
        add_child(text_cursor);
        // Set callbacks
        Input.add_char_callback(this::type_callback);
        Input.add_to_input_map(GLFW_KEY_BACKSPACE, InputCondition.REPEAT, this::delete_callback);
        Input.add_to_input_map(GLFW_KEY_BACKSPACE, InputCondition.PRESS, this::delete_callback);


    }

    @Override
    public boolean clickable() {
        return true;
    }

    @Override
    public void update() {
        boolean mouse_hovering = is_hovering();

        if (mouse_hovering) {
            // When clicked on
            if (Input.get_mouse_pressed(0)) {
                selected = true;
            }
        }
        else {
            // When clicked off
            if (Input.get_mouse_pressed(0))
                selected = false;
        }

        text_cursor.set_position(text.get_dimensions().x, 5);
        text_cursor.set_active(selected);
    }

    private void delete_callback() {
        if (selected)
            text.backspace();
    }

    private void type_callback(String character) {

        if (selected){
            text.concat(character);

            Vector2f text_dimensions = text.get_dimensions();

            if (text_dimensions.x - padding > dimensions.x)
                text.backspace();

        }


        output.set_data_from_string(text.get_text());
    }
}

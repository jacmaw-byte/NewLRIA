package org.game.scheisseRenderer.UI;

import org.game.input.Input;
import org.game.input.InputCondition;
import org.game.scheisseRenderer.TextRenderer;
import org.game.scheisseRenderer.UI.config.UI_Config;
import org.game.scheisseRenderer.UI.containers.OutputContainer;
import org.game.scheisseRenderer.UI.containers.OutputConverter;
import org.game.scheisseRenderer.UI.containers.OutputTypes;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

public class UI_InputField<T extends OutputTypes> extends UI_Element {
    private OutputContainer<T> output;
    private UI_TextLabel text;

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

        output = container;
        output.set_data_from_string(starting_text);

        int font_height = (int)dimensions.y - padding;

        text = new UI_TextLabel(
                "input label",
                new Vector2f(),
                font_height,
                starting_text
        );

        add_child(text);
        Input.add_char_callback(this::type_callback);
        Input.add_to_input_map(GLFW_KEY_BACKSPACE, InputCondition.REPEAT, this::delete_callback);
        Input.add_to_input_map(GLFW_KEY_BACKSPACE, InputCondition.PRESS, this::delete_callback);
    }

    @Override
    public void update() {

    }

    private void delete_callback() {
        text.backspace();
    }

    private void type_callback(String character) {
        text.concat(character);
        output.set_data_from_string(text.get_text());
    }
}

package org.game.scheisseRenderer.UI;

import org.game.scheisseRenderer.TextRenderer;
import org.game.scheisseRenderer.UI.config.UI_Config;
import org.joml.Vector2f;

public class UI_TextLabel extends UI_Element{

    private String text;
    private TextRenderer text_renderer;
    private int padding;
    private int font_size;

    public UI_TextLabel(String name, Vector2f position, int font_size, String text) {
        set_position(position);
        this.text = text;
        this.font_size = font_size;
        this.name = name;

        text_renderer = new TextRenderer(UI_Config.FONT_PATH, font_size);
    }

    public void centralise(Vector2f outer_size) {
        Vector2f dimensions = get_dimensions();
        set_position(
                (outer_size.x - dimensions.x) / 2.0f,
                (outer_size.y - dimensions.y) / 2.0f
        );
    }

    @Override
    public Vector2f get_dimensions(){
        return text_renderer.get_text_dimensions(text);
    }

    @Override
    public void draw() {
        Vector2f screen_position = get_screen_position();
        text_renderer.draw_text(text, screen_position.x, screen_position.y);
    }

    @Override
    public void update() {

    }

    public void set_text(String text) {
        this.text = text;
    }

    public String get_text() {
        return text;
    }

    public void concat(String text) {
        this.text += text;
    }

    public void backspace() {
        if (!text.isEmpty())
            text = text.substring(0, text.length() - 1);
    }
}

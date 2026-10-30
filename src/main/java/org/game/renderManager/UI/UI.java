package org.game.renderManager.UI;

import org.game.renderManager.image.SpriteAtlas;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class UI {
    private static List<UI_Element> root_elements;
    public static SpriteAtlas ui_atlas;

    public static void start(){
        root_elements = new ArrayList<>();
        ui_atlas = new SpriteAtlas("assets/sprites/UI_Atlas.png", new Vector2i(32, 32), 2);

        ui_atlas.bind();
        ui_atlas.upload_to_opengl();
    }

    public static void draw_ui(){
        ui_atlas.bind();

        for (UI_Element element : root_elements){
            element.draw();
        }
    }

    public static void add_element(UI_Element element){
        root_elements.add(element);
    }
}

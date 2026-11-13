package org.game.scheisseRenderer.UI;


import org.game.input.Input;
import org.game.scheisseRenderer.UI.menus.GameMenu;
import org.game.scheisseRenderer.UI.menus.GameUI;
import org.game.scheisseRenderer.image.SpriteAtlas;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.List;

public class UI {
    private HashMap<String, UI_Menu> menus;

    protected static SpriteAtlas ui_atlas;
    protected static UI_Element hovered_element = null;

    public UI(){
        ui_atlas = new SpriteAtlas("assets/sprites/UI_Atlas.png", new Vector2i(32, 32), 2);
        menus = new HashMap<>();

        ui_atlas.bind();
        ui_atlas.upload_to_opengl();
    }

    public void draw_ui() {
        for (UI_Menu menu : menus.values()){
            if (menu.is_active()) menu.draw();
        }
    }

    private UI_Element find_hovered_element() {
        for (UI_Menu menu : menus.values()) {
            if (!menu.is_active()) continue;

            UI_Element element = menu.find_hovered_element();

            if (element != null)
                return element;
        }

        return null;
    }

    public void update_ui() {
        GameMenu.switched = false;

        hovered_element = find_hovered_element();

        if (hovered_element instanceof UI_InputField<?>) {
            Input.set_cursor(Input.ibeam_cursor);
        }
        else {
            Input.set_cursor(Input.arrow_cursor);
        }

        for (UI_Menu menu : menus.values()) {
            if (!menu.is_active()) continue;

            menu.update();
        }
    }

    public UI_Menu get_menu(String name) { return menus.get(name); }
    public void add_menu(UI_Menu menu) { menus.put(menu.get_name(), menu); }
}

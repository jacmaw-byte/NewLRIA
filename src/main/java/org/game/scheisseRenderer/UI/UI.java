package org.game.scheisseRenderer.UI;


import org.game.scheisseRenderer.UI.menus.GameMenu;
import org.game.scheisseRenderer.UI.menus.GameUI;
import org.game.scheisseRenderer.image.SpriteAtlas;
import org.joml.Vector2i;

import java.util.HashMap;

public class UI {
    private HashMap<String, UI_Menu> menus;

    protected static SpriteAtlas ui_atlas;
    protected static boolean hovered_over_element = false;

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

    public void update_ui(){
        GameMenu.switched = false;
        hovered_over_element = false;

        for (UI_Menu menu : menus.values()){
            if (menu.is_active()) menu.update();
        }
    }

    public UI_Menu get_menu(String name) { return menus.get(name); }
    public void add_menu(UI_Menu menu) { menus.put(menu.get_name(), menu); }
}

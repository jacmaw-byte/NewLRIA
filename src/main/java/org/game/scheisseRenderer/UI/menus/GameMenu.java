package org.game.scheisseRenderer.UI.menus;

import org.game.scheisseRenderer.UI.UI_Menu;

public abstract class GameMenu {
    protected UI_Menu menu;
    protected GameUI game_ui;
    public static boolean switched;

    public void switch_menu(GameMenu switched) {
        if (!GameMenu.switched) {
            set_active(false);
            switched.set_active(true);
            GameMenu.switched = true;
        }
    }

    public void set_active(boolean active) { menu.set_active(active); }
    public UI_Menu get_menu() { return menu; }
}

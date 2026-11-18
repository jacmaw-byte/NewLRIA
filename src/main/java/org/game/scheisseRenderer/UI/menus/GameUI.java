package org.game.scheisseRenderer.UI.menus;

import org.game.scheisseRenderer.UI.UI;

public class GameUI {

    protected StartMenu start_menu;
    protected WorldSelectorMenu world_selector_menu;
    protected WorldCreatorMenu world_creator_menu;
    protected GameHUD hud;
    protected ServerSelectorMenu server_selector_menu;

    private UI ui;

    public GameUI() {
        ui = new UI();

        start_menu = new StartMenu(this);
        world_selector_menu = new WorldSelectorMenu(this);
        world_creator_menu = new WorldCreatorMenu(this);
        server_selector_menu = new ServerSelectorMenu(this);
        hud = new GameHUD(this);
    }

    public void update() {
        hud.update();
        ui.update_ui();
    }

    public UI get_ui() { return ui; }
}

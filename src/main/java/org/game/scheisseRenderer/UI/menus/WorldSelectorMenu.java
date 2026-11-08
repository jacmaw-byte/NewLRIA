package org.game.scheisseRenderer.UI.menus;

import org.Constants;
import org.game.scheisseRenderer.UI.UI_Button;
import org.game.scheisseRenderer.UI.UI_Menu;
import org.joml.Vector2f;

public class WorldSelectorMenu extends GameMenu{
    private UI_Button create_world_button;

    public WorldSelectorMenu(GameUI game_ui) {
        this.game_ui = game_ui;

        menu = new UI_Menu("world selector", new Vector2f(), false);
        create_world_button = new UI_Button(
                "create",
                new Vector2f((float) Constants.SCR_WIDTH / 2 - 150, 100f),
                new Vector2f(300f, 50f),
                "Create World",
                this::switch_to_world_creator_menu
        );

        menu.add_element(create_world_button);

        game_ui.get_ui().add_menu(menu);
    }

    public void switch_to_world_creator_menu() {
        switch_menu(game_ui.world_creator_menu);
    }
}

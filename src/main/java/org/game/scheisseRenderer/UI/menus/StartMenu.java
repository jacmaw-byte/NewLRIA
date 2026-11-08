package org.game.scheisseRenderer.UI.menus;

import org.Constants;
import org.game.scheisseRenderer.UI.UI_Button;
import org.game.scheisseRenderer.UI.UI_Menu;
import org.joml.Vector2f;

public class StartMenu extends GameMenu{
    private UI_Button start_button;
    private GameUI game_ui;

    public StartMenu(GameUI game_ui) {
        this.game_ui = game_ui;

        menu = new UI_Menu("start", new Vector2f(), true);
        start_button = new UI_Button(
                "start",
                new Vector2f((float) Constants.SCR_WIDTH / 2 - 150, (float) Constants.SCR_HEIGHT / 2 + 25),
                new Vector2f(300f, 50f),
                "Start Game",
                this::switch_to_world_selector
        );

        menu.add_element(start_button);

        game_ui.get_ui().add_menu(menu);
    }

    private void switch_to_world_selector(){
        switch_menu(game_ui.world_selector_menu);
    }
}

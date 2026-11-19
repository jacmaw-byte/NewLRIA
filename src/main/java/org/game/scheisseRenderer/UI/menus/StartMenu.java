package org.game.scheisseRenderer.UI.menus;

import org.Constants;
import org.game.scheisseRenderer.UI.UI_Button;
import org.game.scheisseRenderer.UI.UI_Menu;
import org.joml.Vector2f;

public class StartMenu extends GameMenu{
    private UI_Button singleplayer_button;
    private UI_Button multiplayer_button;

    private GameUI game_ui;

    public StartMenu(GameUI game_ui) {
        this.game_ui = game_ui;

        menu = new UI_Menu("start", new Vector2f(), true);
        singleplayer_button = new UI_Button(
                "singleplayer",
                new Vector2f((float) Constants.SCR_WIDTH / 2 - 150, (float) Constants.SCR_HEIGHT / 2 + 25),
                new Vector2f(300f, 50f),
                "Singleplayer",
                () -> switch_menu(game_ui.world_selector_menu)
        );

        multiplayer_button = new UI_Button(
                "multiplayer",
                new Vector2f((float) Constants.SCR_WIDTH / 2 - 150, (float) Constants.SCR_HEIGHT / 2 - 30),
                new Vector2f(300f, 50f),
                "Multiplayer",
                () -> switch_menu(game_ui.server_selector_menu)

        );

        menu.add_element(singleplayer_button);
        menu.add_element(multiplayer_button);

        game_ui.get_ui().add_menu(menu);
    }

}

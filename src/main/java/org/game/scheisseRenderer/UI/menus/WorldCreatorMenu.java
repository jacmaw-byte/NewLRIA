package org.game.scheisseRenderer.UI.menus;

import org.Constants;
import org.game.scheisseRenderer.UI.UI_Button;
import org.game.scheisseRenderer.UI.UI_InputField;
import org.game.scheisseRenderer.UI.UI_Menu;
import org.game.scheisseRenderer.UI.UI_TextLabel;
import org.game.scheisseRenderer.UI.containers.IntegerWrapper;
import org.game.scheisseRenderer.UI.containers.OutputContainer;
import org.game.scheisseRenderer.UI.containers.StringWrapper;
import org.game.server.client.Client;
import org.game.server.server.HostServer;
import org.joml.Vector2f;
import org.main.Main;

public class WorldCreatorMenu extends GameMenu{
    private UI_Button create_world_button;
    private UI_TextLabel seed_text_label;

    private OutputContainer<StringWrapper> seed_text_container;
    private OutputContainer<StringWrapper> world_name_container;

    private UI_InputField<StringWrapper> seed_text_field;
    private UI_InputField<StringWrapper> world_name_text_field;

    public WorldCreatorMenu(GameUI game_ui) {
        this.game_ui = game_ui;

        menu = new UI_Menu("world creator", new Vector2f(), false);
        create_world_button = new UI_Button(
                "create",
                new Vector2f((float) Constants.SCR_WIDTH / 2 - 150, 100f),
                new Vector2f(300f, 50f),
                "Create World",
                this::create_world
        );

        seed_text_label = new UI_TextLabel(
            "seed",
                new Vector2f((float) Constants.SCR_WIDTH / 2 - 150, 500f),
                32,
                "Seed:"
        );

        seed_text_container = new OutputContainer<>(StringWrapper::from_string);
        seed_text_field = new UI_InputField<>(
                "seed",
                new Vector2f((float) Constants.SCR_WIDTH / 2, 500f),
                new Vector2f(400f, 50f),
                "",
                seed_text_container,
                3
        );

        world_name_container = new OutputContainer<>(StringWrapper::from_string);
        world_name_text_field = new UI_InputField<>(
                "world name",
                new Vector2f((float) Constants.SCR_WIDTH / 2, 500f + 60),
                new Vector2f(400f, 50f),
                "New World",
                world_name_container,
                10
        );

        menu.add_element(create_world_button);
        menu.add_element(seed_text_field);
        menu.add_element(world_name_text_field);
        menu.add_element(seed_text_label);

        game_ui.get_ui().add_menu(menu);
    }

    public void create_world() {
        if (!GameMenu.switched){
            String seed = seed_text_container.get_data().get_value();

            Main.server = new HostServer(seed);
            Main.server.start();

            Client game_client = new Client(Main.server);

            Main.server.create_host_client(game_client);

            game_client.start();
        }
    }
}

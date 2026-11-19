package org.game.scheisseRenderer.UI.menus;

import org.Constants;
import org.game.scheisseRenderer.UI.UI_Button;
import org.game.scheisseRenderer.UI.UI_TextInputField;
import org.game.scheisseRenderer.UI.UI_Menu;
import org.game.scheisseRenderer.UI.UI_TextLabel;
import org.game.scheisseRenderer.UI.containers.OutputContainer;
import org.game.scheisseRenderer.UI.containers.StringWrapper;
import org.game.server.client.Client;
import org.game.server.client.RemoteServerConnection;
import org.game.server.server.Server;
import org.game.server.server.clientConnection.ServerClientConnection;
import org.joml.Vector2f;

import java.io.IOException;

public class ServerSelectorMenu extends GameMenu {
    private UI_TextLabel ip_address_label;
    private UI_Button join_server_button;

    private OutputContainer<StringWrapper> ip_container;
    private UI_TextInputField<StringWrapper> ip_input_field;

    public ServerSelectorMenu(GameUI game_ui) {
        this.game_ui = game_ui;

        menu = new UI_Menu("Server Selector", new Vector2f(), false);

        ip_address_label = new UI_TextLabel(
                "ip address",
                new Vector2f((float) Constants.SCR_WIDTH / 2 - 50, 517.5f),
                32,
                "ip:"
        );

        join_server_button = new UI_Button(
                "join server",
                new Vector2f((float) Constants.SCR_WIDTH / 2 - 150, 100f),
                new Vector2f(300f, 50f),
                "Join Server",
                this::join_server
        );

        ip_container = new OutputContainer<>(StringWrapper::from_string);
        ip_input_field = new UI_TextInputField<>(
                "ip address",
                new Vector2f((float) Constants.SCR_WIDTH / 2, 500f),
                new Vector2f(400f, 50f),
                "",
                ip_container,
                3
        );

        menu.add_element(ip_address_label);
        menu.add_element(join_server_button);
        menu.add_element(ip_input_field);

        game_ui.get_ui().add_menu(menu);
    }

    private void join_server() {
        RemoteServerConnection server = new RemoteServerConnection();

        Client client = new Client(server);

        try{
            server.connect(ip_container.get_data().get_value(), 25565, client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        switch_menu(game_ui.hud);

        client.start();
    }
}

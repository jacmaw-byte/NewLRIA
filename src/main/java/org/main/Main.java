package org.main;

import org.Constants;
import org.game.input.Input;
import org.game.scheisseRenderer.UI.UI_Button;
import org.game.scheisseRenderer.UI.UI_Menu;
import org.game.scheisseRenderer.UI.menus.GameUI;
import org.game.scheisseRenderer.mainRenderer.MainRenderer;
import org.game.scheisseRenderer.UI.UI;
import org.game.server.client.Client;
import org.game.server.server.HostServer;
import org.joml.Vector2f;

import java.awt.*;

public class Main {

    public static HostServer server;

    public static void main(String[] args){
        Input.start();

        MainRenderer.get_instance().start("LRIA");

        GameUI game_ui = new GameUI();

        MainRenderer.get_instance().set_render_loop(() -> {
            game_ui.get_ui().update_ui();
            game_ui.get_ui().draw_ui();
        });

        MainRenderer.get_instance().start_render_loop();
        server.stop();
    }
}

package org.game.player.client;

import org.game.renderManager.Texture;

import static org.Constants.*;

public class PlayerRenderer {

    private final ClientPlayer player;
    private final Texture screen;

    public PlayerRenderer(ClientPlayer player, Texture screen) {
        this.player = player;
        this.screen = screen;
    }

    public void draw() {

        var data = player.get_data();

        if (data == null) return;

        int draw_x =
                (int)data.x()
                        - (int)player.get_camera().get_position().x
                        + GRID_WIDTH / 2;

        int draw_y =
                (int)data.y()
                        - (int)player.get_camera().get_position().y
                        + GRID_HEIGHT / 2;

        player.get_sprite().draw(draw_x, draw_y, screen);
    }
}
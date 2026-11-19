package org.game.scheisseRenderer.multiplayer;

import org.game.Time;
import org.game.player.client.Camera;

import org.game.player.common.MultiplayerPlayerClientSide;
import org.game.scheisseRenderer.image.SpriteCPU;
import org.game.scheisseRenderer.image.Texture;
import org.joml.Vector2f;
import org.utilities.ExtraMaths;

import java.util.ArrayList;

import static org.Constants.*;

public class OtherPlayerRenderer {
    private ArrayList<MultiplayerPlayerClientSide> players;
    private SpriteCPU player_sprite;
    private Texture screen;
    private Camera camera;

    public OtherPlayerRenderer(SpriteCPU player_sprite, Texture screen, Camera camera) {
        this.camera = camera;
        this.player_sprite = player_sprite;
        this.screen = screen;

        players = new ArrayList<>();
    }

    public void draw() {
        if (players != null) {
            for (MultiplayerPlayerClientSide player : players) {

                Vector2f interpolated_pos = ExtraMaths.lerp(
                        player.last_position,
                        player.player.position(),
                        // Stop the player being drawn beyond their current position
                        Math.min(player.time_since_update / TICK_TIME, 1)
                );

                int draw_x =
                        (int)Math.floor(interpolated_pos.x)
                                - (int)camera.get_position().x
                                + GRID_WIDTH / 2;

                int draw_y =
                        (int)Math.floor(interpolated_pos.y)
                                - (int)camera.get_position().y
                                + GRID_HEIGHT / 2;

                player_sprite.set_flipped_x(player.player.flipped());
                player_sprite.draw(draw_x, draw_y, screen);

                player.time_since_update += Time.delta_time;

            }
        }
    }

    public void set_players(ArrayList<MultiplayerPlayerClientSide> players) {
        this.players = players;
    }
}

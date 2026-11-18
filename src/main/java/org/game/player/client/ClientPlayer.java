package org.game.player.client;

import org.game.player.common.PlayerCollider;
import org.game.player.common.PlayerData;
import org.game.player.common.PlayerInput;
import org.game.scheisseRenderer.image.SpriteCPU;
import org.game.chunkManager.client.ClientChunkManager;
import org.joml.Vector2f;

public class ClientPlayer {

    private final Camera camera;
    private final SpriteCPU sprite;

    private final double base_speed = 150.0;
    private double speed = base_speed;

    private final Vector2f position;
    private final Vector2f velocity;

    private final PlayerCollider collider;
    private boolean flipped;

    private static final float MAX_FALL_SPEED = -2f;   // px per frame (see note), tune to taste


    private boolean in_god_mode = false;

    public ClientPlayer(ClientChunkManager chunk_manager) {

        position = new Vector2f(0, 300);
        velocity = new Vector2f(0, 0);

        camera = new Camera();

        sprite = new SpriteCPU(
                "assets/sprites/Player.png"
        ).slice();

        collider = new PlayerCollider(new ClientTerrainView(chunk_manager));
    }

    private boolean terrain_ready(float dx, float dy) {
        int px = (int) Math.floor(position.x);
        int py = (int) Math.floor(position.y);
        return collider.is_area_loaded(px, py)                                   // where I am
                && collider.is_area_loaded(px + (int) Math.signum(dx) * 2, py)       // where I'm walking
                && collider.is_area_loaded(px, py + (int) Math.floor(dy) - 2);       // where I'm falling/rising
    }

    public void tick(PlayerInput input, double dt) {

        speed = input.sprint()
                ? base_speed * 1.5
                : base_speed;

        float move_dx = 0;
        float move_dy = 0;

        if (input.right()) {
            move_dx += (float)(speed * dt);
            flipped = false;
            collider.set_flipped(false, position);
            sprite.set_flipped_x(false);
        }

        if (input.left()) {
            move_dx -= (float)(speed * dt);
            flipped = true;
            collider.set_flipped(true, position);
            sprite.set_flipped_x(true);
        }

        if (in_god_mode) {

            if (input.up()) {
                move_dy += (float)(speed * dt);
            }

            if (input.down()) {
                move_dy -= (float)(speed * dt);
            }

            position.x += move_dx;
            position.y += move_dy;

            velocity.zero();

        } else {

            velocity.y -= (float) dt * 0.6f;
            velocity.y = Math.max(velocity.y, MAX_FALL_SPEED);   // terminal velocity: can't skip a whole chunk in one frame

            if (!terrain_ready(move_dx, velocity.y)) {
                // Chunks haven't arrived yet: hold position instead of falling through the void.
                velocity.zero();
            } else {
                collider.move(position, move_dx, velocity.y);

                if (collider.hit_y()) velocity.y = 0;

                if (input.jump() && collider.is_grounded()) velocity.y = 0.1f;
            }
        }

        camera.set_position(new Vector2f(position));
        camera.set_chunks_in_view();
    }

    public PlayerData get_data() {
        return new PlayerData(
                position.x,
                position.y,
                velocity.x,
                velocity.y,
                20,
                20,
                speed > base_speed,
                collider.is_grounded()
        );
    }

    public void update(PlayerData new_data) {
        position.set(new_data.x(), new_data.y());
        velocity.zero();
    }

    public void destroy() {
        sprite.destroy();
    }

    public PlayerData get_player_data() {
        return get_data();
    }

    public Vector2f get_position() {
        return position;
    }

    public Camera get_camera() {
        return camera;
    }

    public SpriteCPU get_sprite() {
        return sprite;
    }

    public void set_god_mode(boolean enabled) {
        in_god_mode = enabled;
    }
}
package org.game.player.common;

import org.game.player.client.TerrainView;
import org.joml.Vector2f;

public class PlayerValidator {

    public record Result(PlayerData data, boolean corrected) {}

    // Keep MAX_H_SPEED in sync with ClientPlayer (base_speed * sprint multiplier).
    private static final float MAX_H_SPEED = 150f * 1.5f;
    private static final float MAX_V_SPEED = 2000f;      // generous: falling speed is uncapped client-side
    private static final float SPEED_TOLERANCE = 1.5f;
    private static final float POSITION_SLACK = 4f;      // px
    private static final double MAX_ELAPSED = 1.0;       // s; stops a long gap from banking a huge budget

    private final PlayerCollider collider;
    private final Vector2f trusted = new Vector2f();
    private final Vector2f probe = new Vector2f();
    private long trusted_nanos;
    private boolean has_trusted;

    private boolean noclip_allowed = false;              // enable for host/god mode if you want it
    private boolean input_flipped;

    public PlayerValidator(TerrainView terrain) {
        collider = new PlayerCollider(terrain);
    }

    public void set_noclip_allowed(boolean allowed) { noclip_allowed = allowed; }

    /** Mirrors ClientPlayer: left wins over right, otherwise keep facing. */
    public void note_input(PlayerInput input) {
        if (input.right()) input_flipped = false;
        if (input.left())  input_flipped = true;
    }

    public Result validate(PlayerData player_data) {
        long now = System.nanoTime();

        if (!Float.isFinite(player_data.x()) || !Float.isFinite(player_data.y())) return reject(player_data);

        // First report: accept as spawn (see notes about server-owned spawn).
        int px = (int) Math.floor(player_data.x());
        int py = (int) Math.floor(player_data.y());

        // The client may have refused a flip because it was blocked, so accept either orientation.
        collider.force_flipped(input_flipped);
        if (collider.is_area_loaded(px, py) && collider.overlaps(px, py)) {
            collider.force_flipped(!input_flipped);
            if (collider.overlaps(px, py)) collider.force_flipped(input_flipped);
        }

        boolean loaded = collider.is_area_loaded(px, py);

        if (has_trusted) {
            double elapsed = Math.min((now - trusted_nanos) / 1e9, MAX_ELAPSED);
            float max_dx = (float) (MAX_H_SPEED * elapsed * SPEED_TOLERANCE) + POSITION_SLACK;
            float max_dy = (float) (MAX_V_SPEED * elapsed * SPEED_TOLERANCE) + POSITION_SLACK;

            if (!loaded && (Math.abs(player_data.x() - trusted.x) > POSITION_SLACK
                    || Math.abs(player_data.y() - trusted.y) > POSITION_SLACK))
                return reject(player_data);

            if (Math.abs(player_data.x() - trusted.x) > max_dx || Math.abs(player_data.y() - trusted.y) > max_dy)
                return reject(player_data);

            if (loaded && !noclip_allowed) {
                if (collider.overlaps(px, py)) return reject(player_data);
                if (!collider.path_clear(trusted.x, trusted.y, player_data.x(), player_data.y())) return reject(player_data);
            }
        }

        // Accepted. Only trust the position once its terrain has actually been checked.
        if (loaded) {
            trusted.set(player_data.x(), player_data.y());
            trusted_nanos = now;
            has_trusted = true;
        }

        probe.set(player_data.x(), player_data.y());
        collider.update(probe);
        boolean grounded = collider.is_grounded();

        // Overwrite fields the server can compute itself.
        if (grounded == player_data.grounded()) return new Result(player_data, false);
        return new Result(player_data.with_state(player_data.x(), player_data.y(), player_data.velocity_x(), player_data.velocity_y(), grounded), false);
    }

    private Result reject(PlayerData d) {
        float x = has_trusted ? trusted.x : d.x();
        float y = has_trusted ? trusted.y : d.y();
        probe.set(x, y);
        collider.update(probe);
        trusted_nanos = System.nanoTime();   // don't punish the next frame for time spent rejected
        return new Result(d.with_state(x, y, 0f, 0f, collider.is_grounded()), true);
    }
}
package org.game.player.client;

import org.game.cell.Cell;
import org.game.scheisseRenderer.Texture;
import org.game.scheisseRenderer.image.SpriteCPU;
import org.game.server.client.ClientChunkManager;
import org.types.ColourA;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Pixel-perfect collider.
 *
 * Idea: the player's position is moved one whole pixel at a time, one axis at a
 * time. A move is only accepted if the sprite's outline does not overlap any
 * solid cell at the new position. Nothing is ever pushed back out of terrain,
 * because the player never gets into it.
 *
 * The is_*_colliding() flags are "am I touching something right now" probes
 * (1px outside the sprite), so they are consistent on every side.
 */
//Completely Claude

public class PlayerCollider {

    /** Highest ledge (in pixels) the player will walk up without jumping. 0 disables step-up. */
    private static final int MAX_STEP_HEIGHT = 3;

    /** True if sprite row 0 is the TOP of the image (matches the original collider). */
    private static final boolean ROW_0_IS_TOP = false;

    /** Keeps the position just inside a pixel when blocked while moving in + direction. */
    private static final float PIXEL_EDGE = 0.999f;

    private final ClientPlayer player;
    private final ClientChunkManager chunk_manager;
    private SpriteCPU sprite;

    // facing right (not flipped)
    private int[] outline_right_x = new int[0];
    private int[] outline_right_y = new int[0];
    // facing left (flipped_x == true)
    private int[] outline_left_x  = new int[0];
    private int[] outline_left_y  = new int[0];


    private boolean top_colliding;
    private boolean right_colliding;
    private boolean bottom_colliding;
    private boolean left_colliding;

    private boolean flipped;

    private boolean hit_x;
    private boolean hit_y;

    public PlayerCollider(ClientPlayer player, ClientChunkManager chunk_manager) {
        this.player = player;
        this.chunk_manager = chunk_manager;

        sprite = new SpriteCPU("assets/sprites/Player.png");
        sprite = sprite.slice();
        generate_offsets();
    }

    // ------------------------------------------------------------------
    // Shape
    // ------------------------------------------------------------------

    /** Single place that decides what counts as a solid sprite pixel. Must match Player.draw(). */
    private static boolean is_opaque(ColourA c) {
        return c.a() > 0;
    }

    private static boolean is_opaque_at(SpriteCPU s, int x, int y) {
        if (x < 0 || y < 0 || x >= s.width || y >= s.height) return false;
        return is_opaque(s.get_pixel(x, y));
    }

    /** Collect only the boundary pixels of the sprite; interior pixels can't change an overlap result. */
    public void generate_offsets() {
        int[][] right = build_outline(false);
        outline_right_x = right[0];
        outline_right_y = right[1];

        int[][] left = build_outline(true);
        outline_left_x = left[0];
        outline_left_y = left[1];
    }

    public void debug_draw(Texture screen, int draw_x, int draw_y) {
        int[] ox = flipped ? outline_left_x : outline_right_x;
        int[] oy = flipped ? outline_left_y : outline_right_y;
        ColourA red = new ColourA(255, 0, 0, 255);
        for (int i = 0; i < ox.length; i++)
            screen.set_pixel(draw_x + ox[i], draw_y + oy[i], red);
    }

    /** Returns {xs, ys}. If mirror is true, the sprite is flipped horizontally, exactly as draw() does. */
    private int[][] build_outline(boolean mirror) {
        SpriteCPU s = sprite;
        List<int[]> outline = new ArrayList<>();

        for (int x = 0; x < s.width; x++) {
            for (int y = 0; y < s.height; y++) {
                if (!is_opaque_at(s, x, y)) continue;

                boolean boundary = !is_opaque_at(s, x + 1, y)
                        || !is_opaque_at(s, x - 1, y)
                        || !is_opaque_at(s, x, y + 1)
                        || !is_opaque_at(s, x, y - 1);
                if (!boundary) continue;

                int local_x = mirror ? (s.width - 1) - x : x;   // the only difference between the two outlines
                int local_y = ROW_0_IS_TOP ? (s.height - 1) - y : y;

                outline.add(new int[]{local_x, local_y});
            }
        }

        int[] xs = new int[outline.size()];
        int[] ys = new int[outline.size()];
        for (int i = 0; i < outline.size(); i++) {
            xs[i] = outline.get(i)[0];
            ys[i] = outline.get(i)[1];
        }
        return new int[][]{xs, ys};
    }

    // ------------------------------------------------------------------
    // Queries
    // ------------------------------------------------------------------

    private boolean is_solid(int wx, int wy) {
        Cell cell = chunk_manager.get_cell(wx, wy);
        return cell != null && cell.get_type().collides();
    }

    /** Would the sprite overlap solid terrain with its origin at (px, py)? */
    public boolean overlaps(int px, int py) {
        int[] ox = flipped ? outline_left_x : outline_right_x;
        int[] oy = flipped ? outline_left_y : outline_right_y;

        for (int i = 0; i < ox.length; i++) {
            if (is_solid(px + ox[i], py + oy[i])) return true;
        }
        return false;
    }

    /** Refresh the touching flags. Call after moving. */
    public void update() {
        int px = (int) Math.floor(player.get_position().x);
        int py = (int) Math.floor(player.get_position().y);

        top_colliding = overlaps(px, py + 1);
        bottom_colliding = overlaps(px, py - 1);
        right_colliding = overlaps(px + 1, py);
        left_colliding = overlaps(px - 1, py);
    }

    // ------------------------------------------------------------------
    // Movement
    // ------------------------------------------------------------------

    /**
     * Move pos by (dx, dy), stopping at terrain. Mutates pos.
     * After the call, hit_x() / hit_y() say whether that axis was blocked
     * (so the caller can zero velocity), and the is_*_colliding() flags are fresh.
     */
    public void move(Vector2f pos, float dx, float dy) {
        hit_x = false;
        hit_y = false;

        move_x(pos, dx);
        move_y(pos, dy);

        update();
    }

    private void move_x(Vector2f pos, float dx) {
        if (dx == 0) return;

        final int start_y = (int) Math.floor(pos.y);
        int y = start_y;
        int cur = (int) Math.floor(pos.x);
        final float target = pos.x + dx;
        final int dest = (int) Math.floor(target);
        final int step = dx > 0 ? 1 : -1;

        while (cur != dest) {
            int next = cur + step;

            if (!overlaps(next, y)) {
                cur = next;
                continue;
            }

            // Blocked: try to step up a small ledge, only while standing on something.
            boolean stepped = false;
            if (overlaps(cur, y - 1)) {
                for (int h = 1; h <= MAX_STEP_HEIGHT; h++) {
                    if (!overlaps(next, y + h)) {
                        y += h;
                        cur = next;
                        stepped = true;
                        break;
                    }
                }
            }

            if (!stepped) {
                hit_x = true;
                pos.x = step > 0 ? cur + PIXEL_EDGE : cur;
                pos.y += y - start_y;
                return;
            }
        }

        pos.x = target;
        pos.y += y - start_y;
    }

    public void debug_print_bounds() {
        SpriteCPU s = sprite;

        int min_x = Integer.MAX_VALUE, max_x = Integer.MIN_VALUE;
        for (int x = 0; x < s.width; x++)
            for (int y = 0; y < s.height; y++)
                if (s.get_pixel(x, y).a() > 0) { min_x = Math.min(min_x, x); max_x = Math.max(max_x, x); }

        int omin = Integer.MAX_VALUE, omax = Integer.MIN_VALUE;
        for (int x : outline_right_x) { omin = Math.min(omin, x); omax = Math.max(omax, x); }

        System.out.println("sprite size: " + s.width + "x" + s.height);
        System.out.println("drawn pixels x: " + min_x + ".." + max_x);
        System.out.println("outline x:      " + omin + ".." + omax);
    }

    private void move_y(Vector2f pos, float dy) {
        if (dy == 0) return;

        final int x = (int) Math.floor(pos.x);
        int cur = (int) Math.floor(pos.y);
        final float target = pos.y + dy;
        final int dest = (int) Math.floor(target);
        final int step = dy > 0 ? 1 : -1;

        while (cur != dest) {
            int next = cur + step;

            if (overlaps(x, next)) {
                hit_y = true;
                // Landing: sit exactly on the pixel above the ground. Ceiling: stay just below it.
                pos.y = step > 0 ? cur + PIXEL_EDGE : cur;
                return;
            }
            cur = next;
        }

        pos.y = target;
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    public boolean hit_x() { return hit_x; }
    public boolean hit_y() { return hit_y; }

    public boolean is_top_colliding() { return top_colliding; }
    public boolean is_right_colliding() { return right_colliding; }
    public boolean is_bottom_colliding() { return bottom_colliding; }
    public boolean is_left_colliding() { return left_colliding; }
    public boolean is_grounded() { return bottom_colliding; }


    public void set_flipped(boolean f) {
        if (flipped == f) return;

        int px = (int) Math.floor(player.get_position().x);
        int py = (int) Math.floor(player.get_position().y);

        // Temporarily test the new orientation.
        boolean old_flipped = flipped;
        flipped = f;

        if (overlaps(px, py)) {
            // The flipped outline would enter terrain.
            flipped = old_flipped;
            return;
        }

        // Safe to flip.
        update();
    }

}
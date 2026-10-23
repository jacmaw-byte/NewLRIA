package org.gameManager.player;

import org.gameManager.GameManager;
import org.gameManager.Time;
import org.gameManager.input.InputCondition;
import org.gameManager.renderManager.RenderManager;
import org.gameManager.renderManager.Texture;
import org.gameManager.renderManager.image.SpriteCPU;
import org.types.ColourA;
import org.types.Rect;
import org.types.Vector2I;
import org.types.Vector2f;
import org.lwjgl.stb.*;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import static org.Constants.*;
public class Player {
    private final Camera camera;
    private final double base_speed = 150.0;
    private double speed = base_speed;

    private Vector2f position;
    private Vector2f velocity;

    private SpriteCPU sprite;

    private PlayerCollider collider;
    private boolean flipped_x;

    private boolean in_god_mode = false;

    private float move_dx = 0;
    private float move_dy = 0;

    public void update() {

        if (in_god_mode){
            velocity.x = 0;
            velocity.y = 0;

            position.x += move_dx;
            position.y += move_dy;
        }
        else{
            velocity.y -= 0.2f * (float) Time.delta_time;

            collider.move(position, move_dx, velocity.y);

            if (collider.hit_y()) velocity.y = 0;
        }

        move_dx = 0;
        move_dy = 0;

        camera.set_position(position);
    }

    public Player(){
        camera = new Camera();

        sprite = new SpriteCPU("assets/sprites/Player.png");
        sprite = sprite.slice();

        collider = new PlayerCollider(this);

        collider.debug_print_bounds();

        position = new Vector2f(0, 10);
        velocity = Vector2f.zero;

        flipped_x = false;

        GameManager.get_input_manager().add_to_input_map(GLFW_KEY_D, InputCondition.HOLD, this::move_right);
        GameManager.get_input_manager().add_to_input_map(GLFW_KEY_A, InputCondition.HOLD, this::move_left);
        GameManager.get_input_manager().add_to_input_map(GLFW_KEY_W, InputCondition.HOLD, this::move_up);
        GameManager.get_input_manager().add_to_input_map(GLFW_KEY_S, InputCondition.HOLD, this::move_down);

        GameManager.get_input_manager().add_to_input_map(GLFW_KEY_SPACE, InputCondition.PRESS, this::jump);

        GameManager.get_input_manager().add_to_input_map(GLFW_KEY_LEFT_SHIFT, InputCondition.PRESS, this::sprint);
        GameManager.get_input_manager().add_to_input_map(GLFW_KEY_LEFT_SHIFT, InputCondition.RELEASE, this::stop_sprinting);

        GameManager.get_input_manager().add_to_input_map(GLFW_KEY_F12, InputCondition.PRESS, () -> in_god_mode = !in_god_mode);
    }

    public void destroy(){
        sprite.destroy();
    }

    public void draw(){
        Texture screen = GameManager.get_render_manager().get_screen().get_texture();

        int draw_x = (int)position.x - (int)camera.get_position().x + GRID_WIDTH / 2;
        int draw_y = (int)position.y - (int)camera.get_position().y + GRID_HEIGHT / 2;


        for (int x = 0; x < sprite.width; x++) {
            for (int y = 0; y < sprite.height; y++) {

                int sprite_x = x;
                int sprite_y = y;

                if (flipped_x){
                    sprite_x = (sprite.width - 1) - x;
                }

                ColourA sprite_colour = sprite.get_pixel(sprite_x, sprite_y);

                // Don't draw Transparent cells
                if (sprite_colour.a() > 0){
                    screen.set_pixel(
                            draw_x + x,
                            draw_y + y,
                            sprite_colour
                    );
                }
            }
        }

    }

    public Vector2f get_position(){
        return position;
    }

    public SpriteCPU get_sprite(){
        return sprite;
    }

    public Camera get_camera(){
        return camera;
    }

    public void sprint(){
        speed = base_speed * 1.5;
    }

    public void stop_sprinting(){
        speed = base_speed;
    }

    public void move_right() {
        move_dx += (float)(speed * Time.delta_time);
        flipped_x = false;
        collider.set_flipped(false);
    }
    public void move_left()  {
        move_dx -= (float)(speed * Time.delta_time);
        flipped_x = true;
        collider.set_flipped(true);
    }

    public void move_up() {
        if (in_god_mode)
            move_dy += (float)(speed * Time.delta_time);
    }
    public void move_down()  {
        if (in_god_mode)
            move_dy -= (float)(speed * Time.delta_time);
    }

    public void jump() {
        if (collider.is_grounded()) velocity.y = 0.05f;
    }
}

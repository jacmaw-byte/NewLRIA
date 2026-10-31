package org.game.player.client;

import org.game.player.common.PlayerInput;
import org.game.input.Input;
import org.game.input.InputCondition;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController {

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private boolean jump;
    private boolean sprint;

    public PlayerController() {

        Input.add_to_input_map(
                GLFW_KEY_D, InputCondition.PRESS,
                () -> right = true
        );

        Input.add_to_input_map(
                GLFW_KEY_D, InputCondition.RELEASE,
                () -> right = false
        );

        Input.add_to_input_map(
                GLFW_KEY_A, InputCondition.PRESS,
                () -> left = true
        );

        Input.add_to_input_map(
                GLFW_KEY_A, InputCondition.RELEASE,
                () -> left = false
        );

        Input.add_to_input_map(
                GLFW_KEY_W, InputCondition.PRESS,
                () -> up = true
        );

        Input.add_to_input_map(
                GLFW_KEY_W, InputCondition.RELEASE,
                () -> up = false
        );

        Input.add_to_input_map(
                GLFW_KEY_S, InputCondition.PRESS,
                () -> down = true
        );

        Input.add_to_input_map(
                GLFW_KEY_S, InputCondition.RELEASE,
                () -> down = false
        );

        Input.add_to_input_map(
                GLFW_KEY_SPACE, InputCondition.PRESS,
                () -> jump = true
        );

        Input.add_to_input_map(
                GLFW_KEY_SPACE, InputCondition.RELEASE,
                () -> jump = false
        );

        Input.add_to_input_map(
                GLFW_KEY_LEFT_SHIFT, InputCondition.PRESS,
                () -> sprint = true
        );

        Input.add_to_input_map(
                GLFW_KEY_LEFT_SHIFT, InputCondition.RELEASE,
                () -> sprint = false
        );
    }

    public PlayerInput get_input() {
        return new PlayerInput(
                left,
                right,
                jump,
                up,
                down,
                sprint
        );
    }
}
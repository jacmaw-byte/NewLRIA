package org.gameManager.input;

import java.util.ArrayList;
import java.util.List;

import org.gameManager.GameManager;

import static org.lwjgl.glfw.GLFW.*;

public class InputManager {
    private final List<InputPair> input_hold_map;
    private final List<InputPair> input_press_map;


    public InputManager(){
        input_hold_map = new ArrayList<>();
        input_press_map = new ArrayList<>();
    }

    public void add_to_input_map(int key_code, InputCondition condition, InputCallback callback){
        InputPair pair = new InputPair(new InputCode(key_code, condition), callback);

        if (condition == InputCondition.HOLD)
            input_hold_map.add(pair);
        else
            input_press_map.add(pair);
    }

    public void execute_inputs(){
        long window = GameManager.get_render_manager().get_window();

        for (var pair : input_hold_map){
            if (glfwGetKey(window, pair.code().key_code()) == GLFW_PRESS)
                pair.callback().callback();
        }
    }

    public void process_input(long window, int key, int scancode, int action, int mods){
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
            // Can't integrate this into the input system because it has parameters
            // so it's hard coded
            glfwSetWindowShouldClose(window, true);

        for (InputPair pair : input_press_map){
            if (key == pair.code().key_code() && pair.code().condition() == InputCondition.PRESS && action == GLFW_PRESS)
                pair.callback().callback();
            else if (key == pair.code().key_code() && pair.code().condition() == InputCondition.RELEASE && action == GLFW_RELEASE)
                pair.callback().callback();
        }
    }
}

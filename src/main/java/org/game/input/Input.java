package org.game.input;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static List<InputPair> input_hold_map;
    private static List<InputPair> input_press_map;

    private static double scroll_x = 0.0;
    private static double scroll_y = 0.0;

    public static void start(){
        input_hold_map = new ArrayList<>();
        input_press_map = new ArrayList<>();
    }

    public static void add_to_input_map(int key_code, InputCondition condition, InputCallback callback){
        InputPair pair = new InputPair(new InputCode(key_code, condition), callback);

        if (condition == InputCondition.HOLD)
            input_hold_map.add(pair);
        else
            input_press_map.add(pair);
    }

    public static void pre_polling(){
        // Reset scrolling offsets
        scroll_y = 0;
        scroll_x = 0;
    }

    public static void execute_inputs(long window){
        for (var pair : input_hold_map){
            if (glfwGetKey(window, pair.code().key_code()) == GLFW_PRESS)
                pair.callback().callback();
        }
    }

    public static void process_input(long window, int key, int scancode, int action, int mods){
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

    public static void scroll_callback(long window, double x_offset, double y_offset){
        scroll_x = x_offset;
        scroll_y = y_offset;
    }

    public static double get_scroll_x(){ return scroll_x; }
    public static double get_scroll_y(){ return scroll_y; }

}

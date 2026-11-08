package org.game.input;

import org.Constants;
import org.game.scheisseRenderer.mainRenderer.MainRenderer;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static List<InputPair> input_hold_map;
    private static List<InputPair> input_press_map;

    private static List<CharCallback> char_callbacks;

    private static double scroll_x = 0.0;
    private static double scroll_y = 0.0;

    private static boolean[] mouse_buttons_pressed;

    public static void start(){
        input_hold_map = new ArrayList<>();
        input_press_map = new ArrayList<>();
        char_callbacks = new ArrayList<>();

        mouse_buttons_pressed = new boolean[3];
    }

    public static void add_to_input_map(int key_code, InputCondition condition, InputCallback callback){
        InputPair pair = new InputPair(new InputCode(key_code, condition), callback);

        if (condition == InputCondition.HOLD)
            input_hold_map.add(pair);
        else
            input_press_map.add(pair);
    }

    public static void add_char_callback(CharCallback callback) {
        char_callbacks.add(callback);
    }

    public static void pre_polling(){
        // Reset scrolling offsets
        scroll_y = 0;
        scroll_x = 0;

        for (int i = 0; i < 3; i++) {
            mouse_buttons_pressed[i] = false;
        }
    }

    public static void execute_inputs(long window){
        for (var pair : input_hold_map){
            if (glfwGetKey(window, pair.code().key_code()) == GLFW_PRESS)
                pair.callback().callback();
        }
    }

    public static Vector2i get_mouse_position(){
        try (MemoryStack stack = MemoryStack.stackPush()){
            DoubleBuffer xpos = stack.mallocDouble(1);
            DoubleBuffer ypos = stack.mallocDouble(1);

            glfwGetCursorPos(MainRenderer.get_instance().get_window(), xpos, ypos);

            double dxpos = xpos.get(0);
            double dypos = Constants.SCR_HEIGHT - ypos.get(0);

            return new Vector2i((int)dxpos, (int)dypos);
        }
    }

    private static void process_input(long window, int key, int scancode, int action, int mods){
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
            // Can't integrate this into the input system because it has parameters
            // so it's hard coded
            glfwSetWindowShouldClose(window, true);

        for (InputPair pair : input_press_map){
            if (key == pair.code().key_code() && pair.code().condition() == InputCondition.PRESS && action == GLFW_PRESS)
                pair.callback().callback();
            else if (key == pair.code().key_code() && pair.code().condition() == InputCondition.RELEASE && action == GLFW_RELEASE)
                pair.callback().callback();
            else if (key == pair.code().key_code() && pair.code().condition() == InputCondition.REPEAT && action == GLFW_REPEAT)
                pair.callback().callback();
        }
    }

    private static void mouse_callback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            mouse_buttons_pressed[button] = true;
        }
    }

    private static void scroll_callback(long window, double x_offset, double y_offset){
        scroll_x = x_offset;
        scroll_y = y_offset;
    }

    private static void char_callback(long window, int codepoint){
        for(CharCallback callback : char_callbacks) {
            // Must convert from UTF32 to UTF16, which is what format java chars use
            // Some UTF32 characters are surrogate characters and can only be represented with 2 UTF16 chars
            String string = new String(Character.toChars(codepoint));

            callback.callback(string);
        }
    }

    public static void set_callbacks(long window) {
        glfwSetKeyCallback(window, Input::process_input);
        glfwSetScrollCallback(window, Input::scroll_callback);
        glfwSetMouseButtonCallback(window, Input::mouse_callback);
        glfwSetCharCallback(window, Input::char_callback);
    }

    public static double get_scroll_x(){ return scroll_x; }
    public static double get_scroll_y(){ return scroll_y; }

    public static boolean get_mouse_pressed(int button) {
        return mouse_buttons_pressed[button];
    }

    public static boolean get_mouse_pressing(int button) {
        return glfwGetMouseButton(MainRenderer.get_instance().get_window(), button) == GLFW_PRESS;
    }
}

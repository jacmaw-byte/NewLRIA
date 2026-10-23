package org.gameManager.renderManager;

import org.gameManager.GameManager;
import org.gameManager.Time;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;

import javax.swing.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class RenderManager {
    private final long window;
    public int window_width;
    public int window_height;
    public String window_name;

    private PracticalScreen practical_screen;
    private LightingHandler lighting_handler;

    Callback debugProc;

    public RenderManager(String window_name, int window_width, int window_height){
        this.window_width = window_width;
        this.window_height = window_height;
        this.window_name = window_name;
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_X11);

        if (!glfwInit()){
            throw new IllegalStateException("Unable to initiate GLFW");
        }

        System.out.println(GLFW.glfwGetVersionString());
        System.out.println(GLFW.glfwGetPlatform());

        // Configure GLFW
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);


        // Create glfw window
        window = glfwCreateWindow(this.window_width, this.window_height, this.window_name, NULL, NULL);
        glfwSetKeyCallback(window, GameManager.get_input_manager()::process_input);

        // Make the OpenGl context current
        glfwMakeContextCurrent(window);
        // Disable v-sync
        glfwSwapInterval(0);

        //Make the window visible
        glfwShowWindow(window);
    }

    public long get_window(){
        return window;
    }
    public PracticalScreen get_screen(){
        return practical_screen;
    }
    public LightingHandler get_lighting_handler(){
        return lighting_handler;
    }

    public void start_render_loop(GameLoop loop){
        GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        practical_screen = new PracticalScreen();
        lighting_handler = new LightingHandler();

        Time.last_time = 0.0f;

        double frame_log_interval = 2.0f;
        double frame_log_timer = 0;
        double total = 0.0;
        int i = 0;

        while (!glfwWindowShouldClose(window)){

            // Poll for window events
            glfwPollEvents();
            GameManager.get_input_manager().execute_inputs();

            Time.elapsed_time = glfwGetTime();
            Time.update_delta_time();

            loop.loop();

            // Clears the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            practical_screen.draw(lighting_handler);

            glfwSwapBuffers(window);

            lighting_handler.occlusion_map.clear();
            // Average out the fps over a few seconds
            if (frame_log_timer > frame_log_interval){
                System.out.println(total / i);
                i = 0;
                total = 0.0;
                frame_log_timer = 0;
            }

            total += 1 / Time.delta_time;
            i++;
            frame_log_timer += Time.delta_time;
        }

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        practical_screen.get_texture().destroy();

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }
}

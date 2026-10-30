package org.game;

public class Time {
    public static double delta_time;
    public static double last_time;
    public static double elapsed_time;

    public static void update_delta_time(){
        delta_time = (elapsed_time - last_time);
        last_time = elapsed_time;
    }
}

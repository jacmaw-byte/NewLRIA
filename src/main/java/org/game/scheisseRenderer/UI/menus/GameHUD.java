package org.game.scheisseRenderer.UI.menus;

import org.Constants;
import org.game.Time;
import org.game.globalStats.StatManager;
import org.game.scheisseRenderer.UI.UI_Menu;
import org.game.scheisseRenderer.UI.UI_TextLabel;
import org.joml.Vector2f;

public class GameHUD extends GameMenu{
    private UI_TextLabel fps_counter;

    private double frame_log_rate = 0.5;
    private double frame_time_counter = 0.0;

    private StatManager<Double> frame_stats = new StatManager<>();

    public GameHUD(GameUI game_ui) {
        menu = new UI_Menu("HUD", new Vector2f(), false);
        fps_counter = new UI_TextLabel(
                "fps",
                new Vector2f(100, Constants.SCR_HEIGHT - 50),
                30,
                "fps: "
        );

        menu.add_element(fps_counter);
        game_ui.get_ui().add_menu(menu);
    }

    public void update() {
        frame_time_counter += Time.delta_time;

        frame_stats.update(1 / Time.delta_time);

        if (frame_time_counter > frame_log_rate) {
            fps_counter.set_text("1% lows: " + frame_stats.get_1_percent_low().intValue());
            frame_time_counter = 0.0;
        }
    }
}

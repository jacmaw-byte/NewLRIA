package org.game;

import org.game.chunkManager.ChunkManager;
import org.game.input.Input;
import org.game.player.Player;
import org.game.renderManager.CellRenderer;
import org.game.renderManager.RenderManager;
import org.game.renderManager.UI.UI;
import org.game.terrain.NoiseManager;

public class GameManager {

    private static GameManager instance;

    private ChunkManager chunk_manager;
    private RenderManager render_manager;
    private NoiseManager noise_manager;

    private Player player;

    public static NoiseManager get_noise_manager() { return get_instance().noise_manager; }

    public static RenderManager get_render_manager(){
        return get_instance().render_manager;
    }

    public static ChunkManager get_chunk_manager() {
        return get_instance().chunk_manager;
    }

    public static synchronized GameManager get_instance(){
        return instance;
    }

    public static synchronized void make_instance(){
        instance = new GameManager();
    }

    public void start_game(){
        Input.start();

        render_manager = new RenderManager("thing");
        render_manager.start_context();

        UI.start();

        noise_manager = new NoiseManager();
        chunk_manager = new ChunkManager();

        player = new Player();

        player.get_camera().set_chunk_manager(chunk_manager);

        chunk_manager.set_player(player);

        CellRenderer.set_camera(player.get_camera());

        render_manager.start_render_loop(() -> {
            player.update();

            player.get_camera().set_chunks_in_view();
            chunk_manager.generate_chunks();

            CellRenderer.render_cells();
            player.draw();

            render_manager.get_screen().draw(render_manager.get_lighting_handler());

            UI.draw_ui();

            player.get_camera().clear_chunks_in_view();
        });

        player.destroy();
    }
}

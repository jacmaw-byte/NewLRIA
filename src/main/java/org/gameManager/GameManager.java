package org.gameManager;

import org.gameManager.chunkManager.ChunkManager;
import org.gameManager.input.InputManager;
import org.gameManager.player.Player;
import org.gameManager.renderManager.CellRenderer;
import org.gameManager.renderManager.RenderManager;
import org.gameManager.terrain.NoiseManager;
import org.gameManager.worldGen.FastNoiseLite;

public class GameManager {

    private static GameManager instance;

    private ChunkManager chunk_manager;
    private RenderManager render_manager;
    private InputManager input_manager;
    private NoiseManager noise_manager;

    private Player player;

    public static NoiseManager get_noise_manager() { return get_instance().noise_manager; }

    public static RenderManager get_render_manager(){
        return get_instance().render_manager;
    }

    public static InputManager get_input_manager(){
        return get_instance().input_manager;
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
        input_manager = new InputManager();

        render_manager = new RenderManager("thing", 1920, 1080);

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

            player.get_camera().clear_chunks_in_view();
        });

        player.destroy();
    }
}

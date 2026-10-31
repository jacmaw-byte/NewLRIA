package org.game.server.client;

import org.game.Time;
import org.game.chunkManager.Chunk;
import org.game.input.Input;
import org.game.player.client.ClientPlayer;
import org.game.player.client.PlayerController;
import org.game.player.client.PlayerRenderer;
import org.game.player.common.PlayerData;
import org.game.renderManager.CellRenderer;
import org.game.renderManager.RenderManager;
import org.game.server.server.Server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    private int id;
    private volatile boolean connected;
    private Server server;

    private ClientPlayer player;
    private PlayerController player_controller;
    private PlayerRenderer player_renderer;
    private CellRenderer cell_renderer;

    private RenderManager render_manager;
    private ClientChunkManager chunk_manager;

    // We need these to make sure that the data is processed on a different thread from the server
    private PlayerData pending_player_data = null;
    private final Queue<Chunk> pending_chunks = new ConcurrentLinkedQueue<>();

    public Client(Server server){
        this.server = server;

        render_manager = new RenderManager("LRIA");
        render_manager.start_context();

        chunk_manager = new ClientChunkManager(this);

        player = new ClientPlayer(chunk_manager);
        player_controller = new PlayerController();
        player_renderer = new PlayerRenderer(player, render_manager.get_screen().get_texture());

        cell_renderer = new CellRenderer(this);
    }

    private void poll_chunks(){
        Chunk chunk;

        while ((chunk = pending_chunks.poll()) != null) {
            chunk_manager.receive_chunk(chunk);
        }
    }

    public void start(){

        render_manager.start_render_loop(() -> {
            if (connected){
                poll_chunks();

                if (pending_player_data != null)
                    player.update(pending_player_data);

                server.receive_inputs(id, player_controller.get_input());

                player.tick(player_controller.get_input(), Time.delta_time);

                server.receive_player_data(id, player.get_data());

                player.get_camera().set_chunks_in_view();

                cell_renderer.render_cells();
                player_renderer.draw();

                render_manager.get_screen().draw(render_manager.get_lighting_handler());

                player.get_camera().clear_chunks_in_view();
            }
        });

        player.destroy();
    }

    public void connect(int id) {
        connected = true;
        this.id = id;
    }

    public void receive_chunk(Chunk chunk){ pending_chunks.add(chunk); }
    public void receive_player_data(PlayerData data) { pending_player_data = data; }

    public int get_id(){ return id; }
    public ClientPlayer get_player() { return player; }
    public Server get_server() { return server; }
    public ClientChunkManager get_chunk_manager() { return chunk_manager; }
    public RenderManager get_render_manager() { return render_manager; }
}

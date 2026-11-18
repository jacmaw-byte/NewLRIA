package org.game.server.client;

import org.game.Time;
import org.game.chunkManager.server.ChunkData;
import org.game.chunkManager.client.ClientChunkManager;
import org.game.globalStats.FpsManager;
import org.game.player.client.ClientPlayer;
import org.game.player.client.PlayerController;
import org.game.player.client.PlayerRenderer;
import org.game.player.common.PlayerData;
import org.game.scheisseRenderer.CellRenderer;
import org.game.scheisseRenderer.mainRenderer.MainRenderer;
import org.game.server.server.Server;
import org.main.Main;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    private int id;
    private volatile boolean connected;
    private Server server;

    private ClientPlayer player;
    private PlayerController player_controller;
    private PlayerRenderer player_renderer;
    private CellRenderer cell_renderer;

    private ClientChunkManager chunk_manager;

    // We need these to make sure that the data is processed on a different thread from the server
    private final AtomicReference<PlayerData> pending_player_data = new AtomicReference<>();
    private final Queue<ChunkData> pending_chunks = new ConcurrentLinkedQueue<>();

    public Client(Server server){
        this.server = server;

        chunk_manager = new ClientChunkManager(this);

        player = new ClientPlayer(chunk_manager);
        player_controller = new PlayerController();
        player_renderer = new PlayerRenderer(player, MainRenderer.get_instance().get_screen().get_texture());

        cell_renderer = new CellRenderer(this);
    }

    private void poll_chunks(){
        while (chunk_manager.can_accept_chunk()) {
            ChunkData chunk = pending_chunks.poll();
            if (chunk == null) break;
            chunk_manager.receive_chunk(chunk);
        }
    }

    private void game_loop(){
        if (connected){
            poll_chunks();

            PlayerData correction = pending_player_data.getAndSet(null);

            if (correction != null)
                player.update(correction);

            server.receive_inputs(id, player_controller.get_input());

            player.tick(player_controller.get_input(), Time.delta_time);

            Main.ui.update();

            server.receive_player_data(id, player.get_data());

            player.get_camera().set_chunks_in_view();
            chunk_manager.get_buffer_chunks();

            cell_renderer.render_cells();
            player_renderer.draw();

            MainRenderer.get_instance().get_screen().draw(MainRenderer.get_instance().get_lighting_handler());
            Main.ui.get_ui().draw_ui();

            FpsManager.update(1 / Time.delta_time);

            player.get_camera().clear_chunks_in_view();
            chunk_manager.reset_chunk_throttle();
        }
    }

    public void start(){
        MainRenderer.get_instance().set_render_loop(this::game_loop);
    }

    public void connect(int id) {
        connected = true;
        this.id = id;
    }

    public void server_tick() {
        server.receive_player_data(id, player.get_data());
    }

    public void receive_chunk(ChunkData chunk){
        System.out.println("Adding chunk to pending queue x: " + chunk.position().x + " y: "  + chunk.position().y);
        pending_chunks.add(chunk);
    }

    public void receive_player_data(PlayerData data) {
        pending_player_data.set(data);
    }

    public int get_id(){ return id; }
    public ClientPlayer get_player() { return player; }
    public Server get_server() { return server; }
    public ClientChunkManager get_chunk_manager() { return chunk_manager; }
}

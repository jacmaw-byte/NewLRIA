package org.game.server.server;

import org.game.chunkManager.Chunk;
import org.game.chunkManager.ChunkManager;
import org.game.player.common.PlayerData;
import org.game.player.common.PlayerInput;
import org.game.player.server.ServerPlayer;
import org.game.server.client.Client;
import org.game.server.requests.ChunkRequest;
import org.game.server.requests.InputRequest;
import org.game.server.requests.PlayerDataRequest;
import org.game.server.server.clientInterface.HostClient;
import org.game.server.server.clientInterface.ServerClient;
import org.game.terrain.NoiseManager;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HostServer implements Server, Runnable {
    private final List<ServerClient> clients = new ArrayList<>();
    private final Queue<ServerClient> pending_clients = new ConcurrentLinkedQueue<>();

    private final Queue<ChunkRequest> pending_chunk_requests = new ConcurrentLinkedQueue<>();
    private final Queue<InputRequest> pending_input_requests = new ConcurrentLinkedQueue<>();
    private final Queue<PlayerDataRequest> pending_player_data_requests = new ConcurrentLinkedQueue<>();

    private final ChunkManager chunk_manager = new ChunkManager(this);
    private NoiseManager noise_manager;

    private static final double TICK_TIME = 1.0 / 20.0;
    private volatile boolean running;

    private Thread server_thread;

    public HostServer(String seed) {
        noise_manager = new NoiseManager(seed);
    }

    public void start(){
        running = true;

        server_thread = new Thread(this, "HostServer");
        server_thread.start();
    }

    private void poll_pending_clients(){
        ServerClient client = pending_clients.poll();

        if (client != null){
            client.connect(clients.size());

            clients.add(client);
        }
    }

    private void poll_requested_chunks(){
        ChunkRequest request;

        while ((request = pending_chunk_requests.poll()) != null){
            Chunk chunk = chunk_manager.request_chunk(request.position());

            clients.get(request.client_id()).receive_chunk(chunk);
        }
    }

    private void poll_pending_inputs(){
        InputRequest request;

        while ((request = pending_input_requests.poll()) != null){
            clients.get(request.client_id()).set_last_received_input(request.input());
        }
    }

    private void poll_pending_player_data(){
        PlayerDataRequest request;

        while ((request = pending_player_data_requests.poll()) != null){
            clients.get(request.client_id()).get_player().update(request.data());
        }
    }

    @Override
    public void create_remote_client(){
        // Will implement when I start doing networking
    }

    public void create_host_client(Client client){
        pending_clients.add(new HostClient(this, client));
    }

    @Override
    public void receive_inputs(int client_id, PlayerInput input){
        pending_input_requests.add(new InputRequest(client_id, input));
    }

    @Override
    public void receive_player_data(int client_id, PlayerData data){
        pending_player_data_requests.add(new PlayerDataRequest(client_id, data));
    }

    @Override
    public void request_chunk(int client_id, Vector2i position){
        pending_chunk_requests.add(new ChunkRequest(client_id, position));
    }

    @Override
    public void run() {
        long last_time = System.nanoTime();
        double accumulator = 0;

        while (running){
            long current_time = System.nanoTime();

            double frame_time = (current_time - last_time) / 1_000_000_000.0;

            last_time = current_time;
            // The min reduces stutters
            accumulator += Math.min(frame_time, 0.25);

            // Ensure every tick is processed
            while (accumulator >= TICK_TIME){
                tick(TICK_TIME);
                accumulator -= TICK_TIME;
            }

            // Avoid consuming an entire CPU core
            if (accumulator < TICK_TIME) {
                Thread.yield();
            }
        }
    }

    public void tick(double dela_time){
        poll_pending_clients();
        poll_pending_inputs();
        poll_requested_chunks();
        poll_pending_player_data();

        for (ServerClient client : clients){
            if (client.get_player().get_data() != null){
                Vector2f player_pos = new Vector2f(
                        client.get_player().get_data().x(),
                        client.get_player().get_data().y()
                );

                chunk_manager.generate_chunks_around_player(player_pos);
            }
        }
    }

    public void stop() {
        running = false;

        if (server_thread != null) {
            try {
                server_thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public PlayerData get_player_data(int client_id){
        return clients.get(client_id).get_player().get_data();
    }

    public NoiseManager get_noise_manager(){ return noise_manager; }
    public ChunkManager get_chunk_manager(){ return chunk_manager; }
}

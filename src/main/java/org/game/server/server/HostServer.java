package org.game.server.server;

import org.game.chunkManager.server.ChunkData;
import org.game.chunkManager.server.ServerChunkManager;
import org.game.player.common.MultiplayerPlayer;
import org.game.player.common.PlayerData;
import org.game.player.common.PlayerInput;
import org.game.player.common.PlayerValidator;
import org.game.player.server.ServerPlayer;
import org.game.player.server.ServerTerrainView;
import org.game.server.client.Client;
import org.game.server.requests.ChunkRequest;
import org.game.server.requests.InputRequest;
import org.game.server.requests.PlayerDataRequest;
import org.game.server.server.clientConnection.HostClientConnection;
import org.game.server.server.clientConnection.RemoteClientConnection;
import org.game.server.server.clientConnection.RemoteConnectionsHandler;
import org.game.server.server.clientConnection.ServerClientConnection;
import org.game.terrain.NoiseManager;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.Constants.TICK_TIME;

public class HostServer implements Server, Runnable {
    private final List<ServerClientConnection> clients = new ArrayList<>();
    private final Queue<ServerClientConnection> pending_clients = new ConcurrentLinkedQueue<>();

    private final Queue<ChunkRequest> pending_chunk_requests = new ConcurrentLinkedQueue<>();
    private final Queue<InputRequest> pending_input_requests = new ConcurrentLinkedQueue<>();
    private final Queue<PlayerDataRequest> pending_player_data_requests = new ConcurrentLinkedQueue<>();

    private final ServerChunkManager chunk_manager = new ServerChunkManager(this);
    private NoiseManager noise_manager;

    private RemoteConnectionsHandler remote_connections_handler;

    private volatile boolean running;

    private Thread server_thread;

    public HostServer(String seed) {
        noise_manager = new NoiseManager(seed);
        try {
            remote_connections_handler = new RemoteConnectionsHandler(25565, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void start(){
        running = true;

        remote_connections_handler.start();

        server_thread = new Thread(this, "HostServer");
        server_thread.start();
    }

    private void poll_requested_chunks(){
        ChunkRequest request;

        while ((request = pending_chunk_requests.poll()) != null){
            ChunkData chunk = chunk_manager.request_chunk(request.position()).encode();

            clients.get(request.client_id()).receive_chunk(chunk);
        }
    }

    private void poll_pending_clients(){
        ServerClientConnection client = pending_clients.poll();
        if (client != null){
            client.connect(clients.size());
            client.get_player().set_validator(new PlayerValidator(new ServerTerrainView(chunk_manager)));
            clients.add(client);
        }
    }

    private void poll_pending_inputs(){
        InputRequest request;
        while ((request = pending_input_requests.poll()) != null){
            ServerClientConnection c = clients.get(request.client_id());
            c.set_last_received_input(request.input());
            PlayerValidator v = c.get_player().get_validator();
            if (v != null) v.note_input(request.input());
        }
    }

    private void poll_pending_player_data(){
        PlayerDataRequest request;
        while ((request = pending_player_data_requests.poll()) != null){
            ServerClientConnection client_connection = clients.get(request.client_id());
            ServerPlayer player = client_connection.get_player();

            PlayerValidator.Result result = player.get_validator().validate(request.data());
            player.update(result.data());                       // server always stores the verified version
            if (result.corrected())
                client_connection.receive_corrected_player_data(result.data());
        }
    }

    public void connect_remote_client(RemoteClientConnection client_connection){
        pending_clients.add(client_connection);
    }

    public void create_host_client(Client client){
        pending_clients.add(new HostClientConnection(this, client));
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

        ArrayList<MultiplayerPlayer> players = new ArrayList<>();

        for (ServerClientConnection client : clients){
            client.update_tick();

            if (client.get_player().get_data() != null){
                Vector2f player_pos = new Vector2f(
                        client.get_player().get_data().x(),
                        client.get_player().get_data().y()
                );

                chunk_manager.generate_chunks_around_player(player_pos);

                players.add(new MultiplayerPlayer("player", client.get_player().get_uuid(), player_pos, client.get_player().get_data().flipped()));
            }
        }

        for (ServerClientConnection client : clients) {
            ArrayList<MultiplayerPlayer> other_players = new ArrayList<>();

            for (MultiplayerPlayer player : players) {
                // Don't send the client their own player
                if (player.uuid() != client.get_player().get_uuid()) {
                    other_players.add(player);
                }
            }

            client.receive_other_players(other_players);
        }
    }

    @Override
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
    public ServerChunkManager get_chunk_manager(){ return chunk_manager; }
}

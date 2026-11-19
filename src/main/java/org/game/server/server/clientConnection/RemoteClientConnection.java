
package org.game.server.server.clientConnection;

import org.game.chunkManager.server.ChunkData;
import org.game.player.common.MultiplayerPlayer;
import org.game.player.common.PlayerData;
import org.game.player.common.PlayerInput;
import org.game.player.server.ServerPlayer;
import org.game.server.requests.ChunkRequest;
import org.game.server.requests.PlayerDataRequest;
import org.game.server.requests.networkMessages.*;
import org.game.server.server.HostServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class RemoteClientConnection implements ServerClientConnection {

    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    private volatile PlayerInput last_received_input;
    private final ServerPlayer player;
    private final HostServer server;

    public RemoteClientConnection(Socket socket, HostServer server)
            throws IOException {

        this.socket = socket;
        this.server = server;

        player = new ServerPlayer(UUID.randomUUID());

        last_received_input = new PlayerInput(
                false, false, false, false, false, false
        );

        // Always create output first on BOTH client and server.
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();

        input = new ObjectInputStream(socket.getInputStream());
    }

    public void start() {
        Thread receiver = new Thread(
                this::receiveMessages,
                "Client-" + player.get_uuid() + "-Receiver"
        );

        receiver.start();
    }

    private void receiveMessages() {

        try {
            while (!socket.isClosed()) {

                Object message = input.readObject();

                handleMessage(message);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Connection lost while receiving messages:");
            e.printStackTrace();
            close();
        }
    }

    // Process messages received from the client
    private void handleMessage(Object message) {

        if (message instanceof PlayerInputMessage input_message) {
            set_last_received_input(input_message.input());
        }

        else if (message instanceof ChunkRequestMessage(ChunkRequest request)) {
            server.request_chunk(request.client_id(), request.position());
        }

        else if (message instanceof PlayerDataRequestMessage(PlayerDataRequest request)) {
            server.receive_player_data(request.client_id(), request.data());
            //System.out.println("Player data received");
        }
    }

    // Send an object to the client
    private synchronized void send(Object message) {

        try {
            output.writeObject(message);
            output.flush();
            output.reset();

        } catch (IOException e) {
            close();
        }
    }

    @Override
    public PlayerInput get_last_received_input() {
        return last_received_input;
    }

    @Override
    public void set_last_received_input(PlayerInput input) {
        last_received_input = input;
    }

    @Override
    public ServerPlayer get_player() {
        return player;
    }

    @Override
    public void connect(int id) {
        send(new ConnectMessage(id));
    }

    @Override
    public void receive_chunk(ChunkData chunk) {
        System.out.println("Server sending chunk");
        send(new ChunkMessage(chunk));
    }

    @Override
    public void receive_corrected_player_data(PlayerData data) {
        send(new PlayerDataCorrectionMessage(data));
    }

    @Override
    public void update_tick() {
        send(new TickMessage());
    }

    @Override
    public void receive_other_players(ArrayList<MultiplayerPlayer> players) {
        send(new OtherPlayersMessage(players));
    }

    public void close() {

        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}

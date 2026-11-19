package org.game.server.client;

import org.game.chunkManager.server.ChunkData;
import org.game.player.common.MultiplayerPlayer;
import org.game.player.common.PlayerData;
import org.game.player.common.PlayerInput;
import org.game.server.requests.ChunkRequest;
import org.game.server.requests.PlayerDataRequest;
import org.game.server.requests.networkMessages.*;
import org.game.server.server.Server;
import org.game.server.server.clientConnection.RemoteClientConnection;
import org.joml.Vector2i;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class RemoteServerConnection implements Server {
    private int port;
    private String ip;

    private Client client;

    Socket socket;
    ObjectOutputStream output;
    ObjectInputStream input;

    public void connect(String ip, int port, Client client) throws IOException {
        this.ip = ip;
        this.port = port;
        this.client = client;

        System.out.println("Started connection");

        socket = new Socket(ip, port);

        System.out.println("Opened socket");

        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();

        System.out.println("Opened output");

        input = new ObjectInputStream(socket.getInputStream());

        System.out.println("Opened input");

        start();

        System.out.println("Started ");
    }

    public void start() {
        Thread receiver = new Thread(
                this::receiveMessages,
                "Server-Receiver"
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
            stop();
        }
    }

    // Process messages received from the server
    private void handleMessage(Object message) {

        if (message instanceof TickMessage) {
            client.server_tick();
        }

        else if (message instanceof OtherPlayersMessage(ArrayList<MultiplayerPlayer> players)) {
            client.receive_players(players);
        }

        else if (message instanceof ChunkMessage(ChunkData chunkData)) {
            client.receive_chunk(chunkData);
        }

        else if (message instanceof ConnectMessage(int id)) {
            client.connect(id);
        }

        else if (message instanceof PlayerDataCorrectionMessage(PlayerData data)) {
            client.receive_player_data(data);
            System.out.println("Corrected player data");
        }


    }

    public synchronized void send(Object message) {
        try {
            output.writeObject(message);
            output.flush();
            output.reset();

        } catch (IOException e) {
            stop();
        }
    }

    @Override
    public void receive_inputs(int client_id, PlayerInput input) {
        send(new PlayerInputMessage(client_id, input));
    }

    @Override
    public void request_chunk(int client_id, Vector2i position) {
        send(new ChunkRequestMessage(new ChunkRequest(client_id, position)));
    }

    @Override
    public void receive_player_data(int client_id, PlayerData data) {
        send(new PlayerDataRequestMessage(new PlayerDataRequest(client_id, data)));
    }

    @Override
    public void stop() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}

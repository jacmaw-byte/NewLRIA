package org.game.server.server.clientInterface;

import org.game.chunkManager.Chunk;
import org.game.player.common.PlayerData;
import org.game.player.common.PlayerInput;
import org.game.player.server.ServerPlayer;
import org.game.server.client.Client;
import org.game.server.server.HostServer;
import org.game.server.server.Server;

import java.util.UUID;

// The server's interface with the clients
public class HostClient implements ServerClient {
    private volatile PlayerInput last_received_input;
    private ServerPlayer player;
    private Client client;
    private HostServer server;

    public HostClient(HostServer server, Client client){
        this.server = server;
        this.client = client;

        player = new ServerPlayer(UUID.randomUUID());
        last_received_input = new PlayerInput(false, false, false, false, false, false);
    }

    @Override
    public void connect(int id){
        client.connect(id);
    }

    @Override
    public void receive_chunk(Chunk chunk) { client.receive_chunk(chunk); }

    @Override
    public PlayerInput get_last_received_input() { return last_received_input; }
    @Override
    public void set_last_received_input(PlayerInput input) { last_received_input = input; }

    @Override
    public ServerPlayer get_player() { return player; }
}

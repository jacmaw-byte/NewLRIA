package org.main;

import org.game.input.Input;
import org.game.server.client.Client;
import org.game.server.server.HostServer;

public class Main {
    public static void main(String[] args){
        Input.start();

        HostServer server = new HostServer();
        server.start();

        Client game_client = new Client(server);

        server.create_host_client(game_client);

        game_client.start();

        server.stop();
    }
}

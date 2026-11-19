package org.game.server.server.clientConnection;

import org.game.server.server.HostServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteConnectionsHandler {
    private int port;
    private ServerSocket server_socket;
    private volatile boolean running = true;

    private HostServer server;

    public RemoteConnectionsHandler(int port, HostServer server) throws IOException {
        this.port = port;
        this.server = server;

        server_socket = new ServerSocket(port);
    }

    public void start() {
        Thread connection_thread = new Thread(() -> {
            while (running) {
                try{
                    Socket socket = server_socket.accept();

                    try {

                        System.out.println("client connection received");

                        RemoteClientConnection remote_client_connection = new RemoteClientConnection(socket, server);

                        server.connect_remote_client(remote_client_connection);
                        System.out.println("client connected");

                        remote_client_connection.start();
                        System.out.println("client connection started");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        connection_thread.start();
    }
}

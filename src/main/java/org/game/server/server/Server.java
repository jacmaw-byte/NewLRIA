package org.game.server.server;

import org.game.player.common.PlayerData;
import org.game.player.common.PlayerInput;
import org.joml.Vector2i;

// This is here so any outside clients will be able to use a remote connection class the same as the host client
public interface Server {
    void create_remote_client();
    void receive_inputs(int client_id, PlayerInput input);
    void request_chunk(int client_id, Vector2i position);
    void receive_player_data(int client_id, PlayerData data);
}

package org.game.server.server.clientConnection;

import org.game.chunkManager.server.ChunkData;
import org.game.player.common.PlayerData;
import org.game.player.common.PlayerInput;
import org.game.player.server.ServerPlayer;

public interface ServerClientConnection {
    PlayerInput get_last_received_input();
    void set_last_received_input(PlayerInput input);

    ServerPlayer get_player();

    void connect(int id);

    void receive_chunk(ChunkData chunk);

    void receive_corrected_player_data(PlayerData data);

    void update_tick();
}

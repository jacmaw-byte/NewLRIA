package org.game.server.server.clientConnection;

import org.game.chunkManager.server.ChunkData;
import org.game.player.common.MultiplayerPlayer;
import org.game.player.common.PlayerData;
import org.game.player.common.PlayerInput;
import org.game.player.server.ServerPlayer;

import java.util.ArrayList;

public interface ServerClientConnection {
    PlayerInput get_last_received_input();
    void set_last_received_input(PlayerInput input);
    ServerPlayer get_player();

    void connect(int id);
    void update_tick();

    void receive_chunk(ChunkData chunk);
    void receive_corrected_player_data(PlayerData data);
    void receive_other_players(ArrayList<MultiplayerPlayer> players);

}

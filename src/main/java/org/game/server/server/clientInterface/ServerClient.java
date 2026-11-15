package org.game.server.server.clientInterface;

import org.game.chunkManager.server.ChunkData;
import org.game.chunkManager.server.ServerChunk;
import org.game.player.common.PlayerInput;
import org.game.player.server.ServerPlayer;

public interface ServerClient {
    PlayerInput get_last_received_input();
    void set_last_received_input(PlayerInput input);

    ServerPlayer get_player();

    void connect(int id);

    void receive_chunk(ChunkData chunk);
}

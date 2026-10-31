package org.game.player.server;

import org.game.player.common.PlayerData;

import java.util.UUID;

public class ServerPlayer {

    private final UUID uuid;
    private PlayerData data;

    public ServerPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public void update(PlayerData new_data) {
        data = new_data;
    }

    public PlayerData get_data() {
        return data;
    }

    public UUID get_uuid() {
        return uuid;
    }
}
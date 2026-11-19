package org.game.server.requests;

import org.game.player.common.PlayerData;

import java.io.Serializable;

public record PlayerDataRequest(
   int client_id,
   PlayerData data
) implements Serializable {}

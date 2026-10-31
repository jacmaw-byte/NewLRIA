package org.game.server.requests;

import org.game.player.common.PlayerData;

public record PlayerDataRequest(
   int client_id,
   PlayerData data
) {}

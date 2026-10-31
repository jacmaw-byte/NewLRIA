package org.game.server.requests;

import org.game.player.common.PlayerInput;

public record InputRequest(
        int client_id,
        PlayerInput input
) {}
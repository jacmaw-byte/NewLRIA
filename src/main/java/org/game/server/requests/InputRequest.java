package org.game.server.requests;

import org.game.player.common.PlayerInput;

import java.io.Serializable;

public record InputRequest(
        int client_id,
        PlayerInput input
) implements Serializable {}
package org.game.server.requests;

import org.joml.Vector2i;

import java.io.Serializable;

public record ChunkRequest(
    int client_id,
    Vector2i position
) implements Serializable {}
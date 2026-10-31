package org.game.server.requests;

import org.joml.Vector2i;

public record ChunkRequest(
    int client_id,
    Vector2i position
) {}
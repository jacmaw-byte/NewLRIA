package org.game.server.requests.networkMessages;

import org.game.player.common.PlayerData;

public record PlayerDataCorrectionMessage(
        PlayerData data
) implements NetworkMessage{
}

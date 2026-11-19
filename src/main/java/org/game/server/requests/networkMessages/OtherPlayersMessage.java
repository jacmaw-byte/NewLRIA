package org.game.server.requests.networkMessages;

import org.game.player.common.MultiplayerPlayer;

import java.util.ArrayList;

public record OtherPlayersMessage(
        ArrayList<MultiplayerPlayer> players
) implements NetworkMessage {
}

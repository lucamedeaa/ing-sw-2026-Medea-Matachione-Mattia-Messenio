package it.polimi.ingsw.network.messages;

import java.io.Serial;

import it.polimi.ingsw.network.server.ConnectionState;

public record JoinGameMessage(String nickname, String gameId) implements ClientMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void dispatchTo(ConnectionState state) {
        state.joinGame(nickname, gameId);
    }
}

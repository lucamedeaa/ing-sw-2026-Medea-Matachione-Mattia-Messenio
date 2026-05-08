package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.server.ConnectionState;

public record JoinGameMessage(String nickname, String gameId) implements ClientMessage {
    @Override
    public void dispatchTo(ConnectionState state) {
        state.joinGame(nickname, gameId);
    }
}

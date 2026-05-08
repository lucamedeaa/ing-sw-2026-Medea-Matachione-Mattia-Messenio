package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.server.ConnectionState;

public record CreateGameMessage(String nickname, int maxPlayers) implements ClientMessage {
    @Override
    public void dispatchTo(ConnectionState state) {
        state.createGame(nickname, maxPlayers);
    }
}

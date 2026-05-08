package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.server.ConnectionState;

public record TakeCardMessage(int row, int col) implements ClientMessage {
    @Override
    public void dispatchTo(ConnectionState state) {
        state.takeCard(row, col);
    }
}

package it.polimi.ingsw.common.message.client;

import java.io.Serial;

import it.polimi.ingsw.server.network.state.ConnectionState;

public record TakeCardMessage(int row, int col) implements ClientMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void dispatchTo(ConnectionState state) {
        state.takeCard(row, col);
    }
}

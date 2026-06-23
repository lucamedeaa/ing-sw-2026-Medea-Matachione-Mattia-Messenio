package it.polimi.ingsw.common.message.client;

import java.io.Serial;

import it.polimi.ingsw.server.network.state.ConnectionState;

/**
 * Immutable data transfer object for take card message.
 *
 * @param row board row index
 * @param col card column index
 */
public record TakeCardMessage(int row, int col) implements ClientMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void dispatchTo(ConnectionState state) {
        state.takeCard(row, col);
    }
}

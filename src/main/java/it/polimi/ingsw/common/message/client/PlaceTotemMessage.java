package it.polimi.ingsw.common.message.client;

import java.io.Serial;

import it.polimi.ingsw.server.network.state.ConnectionState;

/**
 * Immutable data transfer object for place totem message.
 *
 * @param positionIndex offer tile position index
 */
public record PlaceTotemMessage(int positionIndex) implements ClientMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void dispatchTo(ConnectionState state) {
        state.placeTotem(positionIndex);
    }
}

package it.polimi.ingsw.common.message.client;

import java.io.Serial;

import it.polimi.ingsw.server.network.state.ConnectionState;

/** Immutable data transfer object for leave game message. */
public record LeaveGameMessage() implements ClientMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void dispatchTo(ConnectionState state) {
        state.leaveGame();
    }
}

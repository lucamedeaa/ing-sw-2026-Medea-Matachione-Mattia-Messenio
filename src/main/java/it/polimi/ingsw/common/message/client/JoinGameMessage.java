package it.polimi.ingsw.common.message.client;

import java.io.Serial;

import it.polimi.ingsw.server.network.state.ConnectionState;

/**
 * Immutable data transfer object for join game message.
 *
 * @param nickname player nickname
 * @param gameId game identifier
 */
public record JoinGameMessage(String nickname, String gameId) implements ClientMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void dispatchTo(ConnectionState state) {
        state.joinGame(nickname, gameId);
    }
}

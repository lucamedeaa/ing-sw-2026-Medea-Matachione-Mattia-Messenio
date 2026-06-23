package it.polimi.ingsw.common.message.client;

import java.io.Serial;

import it.polimi.ingsw.server.network.state.ConnectionState;

/**
 * Immutable data transfer object for create game message.
 *
 * @param nickname player nickname
 * @param maxPlayers maximum number of players
 */
public record CreateGameMessage(String nickname, int maxPlayers) implements ClientMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void dispatchTo(ConnectionState state) {
        state.createGame(nickname, maxPlayers);
    }
}

package it.polimi.ingsw.server.network.state;

import it.polimi.ingsw.server.network.ConnectionContext;

import java.util.Objects;

/** Default command behavior for states where a client command is not valid. */
public abstract class UnsupportedConnectionCommands implements ConnectionState {

    private static final String INVALID_STATE_COMMAND = "Command not valid in the current connection state.";

    private final ConnectionContext connection;

    /**
     * Creates a new {@code UnsupportedConnectionCommands} instance.
     *
     * @param connection client connection context
     */
    public UnsupportedConnectionCommands(ConnectionContext connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    /** {@inheritDoc} */
    @Override
    public void createGame(String nickname, int maxPlayers) {
        reject();
    }

    /** {@inheritDoc} */
    @Override
    public void joinGame(String nickname, String gameId) {
        reject();
    }

    /** {@inheritDoc} */
    @Override
    public void getAvailableGames() {
        reject();
    }

    /** {@inheritDoc} */
    @Override
    public void leaveGame() {
        reject();
    }

    /** {@inheritDoc} */
    @Override
    public void placeTotem(int positionIndex) {
        reject();
    }

    /** {@inheritDoc} */
    @Override
    public void takeCard(int row, int col) {
        reject();
    }

    /** {@inheritDoc} */
    @Override
    public void skipAction() {
        reject();
    }

    /** {@inheritDoc} */
    @Override
    public void getLeaderboard() {
        reject();
    }

    private void reject() {
        connection.error(INVALID_STATE_COMMAND);
    }
}

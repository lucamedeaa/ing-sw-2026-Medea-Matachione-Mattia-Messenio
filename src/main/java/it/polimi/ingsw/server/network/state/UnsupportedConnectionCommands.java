package it.polimi.ingsw.server.network.state;

import it.polimi.ingsw.server.network.ConnectionContext;

import java.util.Objects;

/** Default command behavior for states where a client command is not valid. */
public abstract class UnsupportedConnectionCommands implements ConnectionState {

    private static final String INVALID_STATE_COMMAND = "Command not valid in the current connection state.";

    private final ConnectionContext connection;

    public UnsupportedConnectionCommands(ConnectionContext connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        reject();
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        reject();
    }

    @Override
    public void getAvailableGames() {
        reject();
    }

    @Override
    public void leaveGame() {
        reject();
    }

    @Override
    public void placeTotem(int positionIndex) {
        reject();
    }

    @Override
    public void takeCard(int row, int col) {
        reject();
    }

    @Override
    public void skipAction() {
        reject();
    }

    @Override
    public void getLeaderboard() {
        reject();
    }

    private void reject() {
        connection.error(INVALID_STATE_COMMAND);
    }
}

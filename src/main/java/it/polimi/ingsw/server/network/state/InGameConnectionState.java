package it.polimi.ingsw.server.network.state;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.network.ConnectionContext;

/**
 * Connection state that accepts in-game commands for one player.
 */
public class InGameConnectionState extends UnsupportedConnectionCommands {

    private final String nickname;
    private final ConnectionContext connection;
    private final GameController gameController;

    /**
     * Creates the in-game state.
     *
     * @param nickname player nickname bound to this connection
     * @param connection connection context
     * @param gameController controller for the current game
     */
    public InGameConnectionState(String nickname, ConnectionContext connection, GameController gameController) {
        super(connection);
        this.nickname = nickname;
        this.connection = connection;
        this.gameController = gameController;
    }

    @Override
    public void leaveGame() {
        connection.transitionToLobby();
        connection.gameLeftSuccess("Returned to lobby.");
        gameController.handlePlayerDisconnection(nickname);
    }

    @Override
    public void placeTotem(int positionIndex) {
        gameController.handlePlaceTotem(nickname, positionIndex, this::sendMoveError);
    }

    @Override
    public void takeCard(int row, int col) {
        gameController.handleTakeCard(nickname, row, col, this::sendMoveError);
    }

    @Override
    public void skipAction() {
        gameController.handleSkipBonus(nickname, this::sendMoveError);
    }

    @Override
    public void handleDisconnection() {
        connection.clearNickname();
        gameController.handlePlayerDisconnection(nickname);
    }

    private void sendMoveError(String errorMessage) {
        connection.error("Move error: " + errorMessage);
    }
}

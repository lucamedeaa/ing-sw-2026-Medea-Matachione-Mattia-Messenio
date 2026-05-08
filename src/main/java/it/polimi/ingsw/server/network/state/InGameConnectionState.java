package it.polimi.ingsw.server.network.state;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.network.ConnectionContext;

public class InGameConnectionState implements ConnectionState {

    private final String nickname;
    private final ConnectionContext connection;
    private final GameController gameController;

    public InGameConnectionState(String nickname, ConnectionContext connection, GameController gameController) {
        this.nickname = nickname;
        this.connection = connection;
        this.gameController = gameController;
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        connection.error("Already in game.");
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        connection.error("Already in game.");
    }

    @Override
    public void getAvailableGames() {
        connection.error("Already in game.");
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
    public void getLeaderboard() {
        connection.error("Game is still running.");
    }

    @Override
    public void handleDisconnection() {
        connection.clearNickname();
        gameController.handlePlayerDisconnection(nickname);
    }

    private void sendMoveError(String errorMessage) {
        connection.error("Errore mossa: " + errorMessage);
    }
}

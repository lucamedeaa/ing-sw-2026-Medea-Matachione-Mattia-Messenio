package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.network.messages.AfterGameMessage;
import it.polimi.ingsw.network.messages.InGameMessage;
import it.polimi.ingsw.network.messages.MatchmakingMessage;
import it.polimi.ingsw.network.visitor.InGameVisitor;

public class InGameConnectionState implements ConnectionState {

    private final String nickname;
    private final ConnectionContext connection;
    private final GameController gameController;
    private final InGameVisitor socketGameActionVisitor;

    public InGameConnectionState(String nickname, ConnectionContext connection, GameController gameController) {
        this.nickname = nickname;
        this.connection = connection;
        this.gameController = gameController;
        this.socketGameActionVisitor = new SocketGameActionVisitor(this);
    }

    @Override
    public void handle(MatchmakingMessage message) {
        connection.error("Already in game. Cannot send matchmaking messages.");
    }

    @Override
    public void handle(InGameMessage message) {
        message.accept(socketGameActionVisitor);
    }

    @Override
    public void handle(AfterGameMessage message) {
        connection.error("Game is still running.");
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

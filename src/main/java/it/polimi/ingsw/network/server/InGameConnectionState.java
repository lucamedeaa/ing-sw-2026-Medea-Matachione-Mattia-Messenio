package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.network.messages.InGameMessage;
import it.polimi.ingsw.network.messages.MatchmakingMessage;
import it.polimi.ingsw.network.visitor.InGameVisitor;

public class InGameConnectionState implements ConnectionState {

    private final String nickname;
    private final ClientProxy client;
    private final GameController gameController;
    private final InGameVisitor socketGameActionVisitor;

    public InGameConnectionState(String nickname, ClientProxy client, GameController gameController) {
        this.nickname = nickname;
        this.client = client;
        this.gameController = gameController;
        this.socketGameActionVisitor = new SocketGameActionVisitor(this);
    }

    @Override
    public void handle(MatchmakingMessage message) {
        client.error("Already in game. Cannot send matchmaking messages.");
    }

    @Override
    public void handle(InGameMessage message) {
        message.accept(socketGameActionVisitor);
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        client.error("Already in game.");
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        client.error("Already in game.");
    }

    @Override
    public void getAvailableGames() {
        client.error("Already in game.");
    }

    @Override
    public void leaveGame() {
        client.error("Already in game.");
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
        gameController.handlePlayerDisconnection(nickname);
    }

    private void sendMoveError(String errorMessage) {
        client.error("Errore mossa: " + errorMessage);
    }
}

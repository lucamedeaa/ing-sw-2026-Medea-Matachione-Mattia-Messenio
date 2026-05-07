package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.AfterGameMessage;
import it.polimi.ingsw.network.messages.InGameMessage;
import it.polimi.ingsw.network.messages.MatchmakingMessage;
import it.polimi.ingsw.network.visitor.AfterGameVisitor;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

public class AfterGameConnectionState implements ConnectionState {
    private final ConnectionContext connection;
    private final LeaderboardService leaderboardService;
    private final int playerCount;
    private final AfterGameVisitor afterGameVisitor;

    public AfterGameConnectionState(
            ConnectionContext connection,
            int playerCount,
            LeaderboardService leaderboardService
    ) {
        this.connection = connection;
        this.playerCount = playerCount;
        this.leaderboardService = leaderboardService;
        this.afterGameVisitor = new SocketAfterGameVisitor(this);
    }

    @Override
    public void handle(MatchmakingMessage message) {
        connection.error("Return to lobby before matchmaking.");
    }

    @Override
    public void handle(InGameMessage message) {
        connection.error("Game has already ended.");
    }

    @Override
    public void handle(AfterGameMessage message) {
        message.accept(afterGameVisitor);
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        connection.error("Return to lobby before matchmaking.");
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        connection.error("Return to lobby before matchmaking.");
    }

    @Override
    public void getAvailableGames() {
        connection.error("Return to lobby before matchmaking.");
    }

    @Override
    public void leaveGame() {
        connection.transitionToLobby();
        connection.gameLeftSuccess("Returned to lobby.");
    }

    @Override
    public void placeTotem(int positionIndex) {
        connection.error("Game has already ended.");
    }

    @Override
    public void takeCard(int row, int col) {
        connection.error("Game has already ended.");
    }

    @Override
    public void skipAction() {
        connection.error("Game has already ended.");
    }

    @Override
    public void getLeaderboard() {
        connection.leaderboard(leaderboardService.getLeaderboard(playerCount));
    }

    @Override
    public void handleDisconnection() {
        connection.clearNickname();
    }
}

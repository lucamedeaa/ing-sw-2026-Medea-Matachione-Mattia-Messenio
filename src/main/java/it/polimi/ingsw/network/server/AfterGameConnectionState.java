package it.polimi.ingsw.network.server;

import it.polimi.ingsw.server.leaderboard.LeaderboardService;

public class AfterGameConnectionState implements ConnectionState {
    private final ConnectionContext connection;
    private final LeaderboardService leaderboardService;
    private final int playerCount;

    public AfterGameConnectionState(
            ConnectionContext connection,
            int playerCount,
            LeaderboardService leaderboardService
    ) {
        this.connection = connection;
        this.playerCount = playerCount;
        this.leaderboardService = leaderboardService;
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

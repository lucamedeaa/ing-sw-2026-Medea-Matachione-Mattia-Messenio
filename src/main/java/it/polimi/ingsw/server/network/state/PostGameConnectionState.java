package it.polimi.ingsw.server.network.state;

import it.polimi.ingsw.server.leaderboard.LeaderboardService;
import it.polimi.ingsw.server.network.ConnectionContext;

/**
 * Connection state available after a game has completed.
 */
public class PostGameConnectionState extends UnsupportedConnectionCommands {
    private final ConnectionContext connection;
    private final LeaderboardService leaderboardService;
    private final int playerCount;

    /**
     * Creates the post-game state.
     *
     * @param connection connection context
     * @param playerCount player count of the completed game
     * @param leaderboardService leaderboard service for post-game leaderboard requests
     */
    public PostGameConnectionState(
            ConnectionContext connection,
            int playerCount,
            LeaderboardService leaderboardService
    ) {
        super(connection);
        this.connection = connection;
        this.playerCount = playerCount;
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void leaveGame() {
        connection.transitionToLobby();
        connection.gameLeftSuccess("Returned to lobby.");
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

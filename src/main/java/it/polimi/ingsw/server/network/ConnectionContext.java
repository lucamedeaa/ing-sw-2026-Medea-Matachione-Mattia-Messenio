package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

/**
 * Mutable connection context shared by server connection states.
 */
public interface ConnectionContext extends ClientProxy {

    /**
     * Moves this connection to the in-game command state.
     *
     * @param gameController controller for the started game
     */
    void transitionToGameState(GameController gameController);

    /**
     * Moves this connection to the post-game command state.
     *
     * @param playerCount player count of the completed game
     * @param leaderboardService leaderboard service used for post-game requests
     */
    void transitionToAfterGameState(int playerCount, LeaderboardService leaderboardService);

    /**
     * Stores the nickname currently associated with this connection.
     *
     * @param nickname nickname to store, or null to clear it without unregistering
     */
    void setNickname(String nickname);

    /**
     * Returns the nickname associated with this connection.
     *
     * @return current nickname, or null before matchmaking
     */
    String getNickname();

    /**
     * Moves this connection back to matchmaking/lobby state.
     */
    void transitionToLobby();

    /**
     * Clears and unregisters the current nickname, if present.
     */
    void clearNickname();

}

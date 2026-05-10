package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.GameController;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

/**
 * Client proxy with room lifecycle transitions used by lobby rooms.
 */
public interface RoomClientProxy extends ClientProxy {

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
     * Moves this connection back to matchmaking/lobby state.
     */
    void transitionToLobby();
}

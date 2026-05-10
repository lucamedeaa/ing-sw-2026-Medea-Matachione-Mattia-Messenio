package it.polimi.ingsw.server.network.state;

/**
 * State-specific handler for commands received from a client connection.
 */
public interface ConnectionState {

    /**
     * Handles a create-game request.
     *
     * @param nickname player nickname
     * @param maxPlayers requested room size
     */
    void createGame(String nickname, int maxPlayers);

    /**
     * Handles a join-game request.
     *
     * @param nickname player nickname
     * @param gameId target game identifier
     */
    void joinGame(String nickname, String gameId);

    /**
     * Handles a request for available games.
     */
    void getAvailableGames();

    /**
     * Handles a request to leave the current context.
     */
    void leaveGame();

    /**
     * Handles a totem-placement request.
     *
     * @param positionIndex offer-track position
     */
    void placeTotem(int positionIndex);

    /**
     * Handles a take-card request.
     *
     * @param row board row index
     * @param col board column index
     */
    void takeCard(int row, int col);

    /**
     * Handles a request to skip the optional action.
     */
    void skipAction();

    /**
     * Handles a leaderboard request.
     */
    void getLeaderboard();

    /**
     * Cleans up state after the underlying connection is closed.
     */
    void handleDisconnection();
}

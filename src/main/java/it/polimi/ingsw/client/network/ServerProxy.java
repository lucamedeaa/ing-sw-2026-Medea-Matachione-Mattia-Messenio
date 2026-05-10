package it.polimi.ingsw.client.network;

/**
 * Client-side abstraction over the server transport.
 */
public interface ServerProxy {

    /**
     * Requests creation of a new game room.
     *
     * @param nickname nickname of the creating player
     * @param maxPlayers maximum room size
     */
    void createGame(String nickname, int maxPlayers);

    /**
     * Requests to join an existing game room.
     *
     * @param nickname nickname of the joining player
     * @param gameId target game identifier
     */
    void joinGame(String nickname, String gameId);

    /**
     * Requests the current list of joinable games.
     */
    void getAvailableGames();

    /**
     * Leaves the current lobby, game, or post-game room.
     */
    void leaveGame();

    /**
     * Places the player's totem on an offer-track position.
     *
     * @param positionIndex zero-based offer-track position
     */
    void placeTotem(int positionIndex);

    /**
     * Takes a card from the board.
     *
     * @param row board row index
     * @param col board column index
     */
    void takeCard(int row, int col);

    /**
     * Skips the current optional action.
     */
    void skipAction();

    /**
     * Requests the leaderboard for the completed game category.
     */
    void getLeaderboard();

    /**
     * Closes the connection to the server.
     */
    void disconnect();
}

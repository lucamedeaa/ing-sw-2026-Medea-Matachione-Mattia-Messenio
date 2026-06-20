package it.polimi.ingsw.client.view.tui.command;

/**
 * Server-facing command port used by the TUI.
 */
public interface ServerCommandPort {
    /**
     * Requests creation of a new game.
     *
     * @param nickname nickname chosen by the player
     * @param maxPlayers maximum number of players in the room
     */
    void createGame(String nickname, int maxPlayers);

    /**
     * Requests to join an existing game.
     *
     * @param nickname nickname chosen by the player
     * @param gameId target game identifier
     */
    void joinGame(String nickname, String gameId);

    /**
     * Requests the list of joinable games.
     */
    void getAvailableGames();

    /**
     * Leaves the current room or match.
     */
    void leaveGame();

    /**
     * Places the local player's totem on the selected offer-track position.
     *
     * @param posIdx target tile index
     */
    void placeTotem(int posIdx);

    /**
     * Takes a card from the board.
     *
     * @param row row index, where 0 is upper row and 1 is lower row
     * @param col column index in the selected row
     */
    void takeCard(int row, int col);

    /**
     * Skips the current optional action.
     */
    void skipAction();

    /**
     * Requests the leaderboard for the current player count.
     */
    void getLeaderboard();

    /**
     * Disconnects from the server and invokes the callback when completed.
     *
     * @param completionCallback callback executed after disconnection completes
     */
    void disconnect(Runnable completionCallback);
}

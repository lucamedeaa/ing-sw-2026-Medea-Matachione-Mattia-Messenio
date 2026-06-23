package it.polimi.ingsw.client.controller;
import it.polimi.ingsw.client.view.tui.command.ServerCommandPort;
import it.polimi.ingsw.client.network.ServerProxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Dispatches client commands to the server on a dedicated network executor. */
public class ServerController implements ServerCommandPort {

    private final ServerProxy server;
    private final ExecutorService networkExecutor;

    /**
     * Creates a new {@code ServerController} instance.
     *
     * @param server server proxy or command port
     */
    public ServerController(ServerProxy server) {
        this.server = server;
        this.networkExecutor = Executors.newSingleThreadExecutor();
    }

    private void runAsync(Runnable action) {
        networkExecutor.submit(action);
    }

    /**
     * Requests creation of a new game.
     *
     * @param nickname player nickname
     * @param maxPlayers maximum number of players
     */
    public void createGame(String nickname, int maxPlayers) {
        runAsync(() -> server.createGame(nickname, maxPlayers));
    }

    /**
     * Requests to join an existing game.
     *
     * @param nickname player nickname
     * @param gameId game identifier
     */
    public void joinGame(String nickname, String gameId) {
        runAsync(() -> server.joinGame(nickname, gameId));
    }

    /** Requests the list of available games. */
    public void getAvailableGames() {
        runAsync(server::getAvailableGames);
    }

    /** Requests to leave the current game. */
    public void leaveGame() {
        runAsync(server::leaveGame);
    }

    /**
     * Requests to place the local player's totem.
     *
     * @param posIdx offer tile position index
     */
    public void placeTotem(int posIdx) {
        runAsync(() -> server.placeTotem(posIdx));
    }

    /**
     * Requests to take a card from the board.
     *
     * @param row board row index
     * @param col card column index
     */
    public void takeCard(int row, int col) {
        runAsync(() -> server.takeCard(row, col));
    }

    /** Requests to skip the current optional action. */
    public void skipAction() {
        runAsync(server::skipAction);
    }

    /** Requests the leaderboard from the server. */
    public void getLeaderboard() {
        runAsync(server::getLeaderboard);
    }

    /**
     * Disconnects from the server and optionally runs a completion callback.
     *
     * @param completionCallback callback invoked when disconnection completes
     */
    public void disconnect(Runnable completionCallback) {
        networkExecutor.submit(() -> {
            server.disconnect();
            if (completionCallback != null) completionCallback.run();
        });
    }
}

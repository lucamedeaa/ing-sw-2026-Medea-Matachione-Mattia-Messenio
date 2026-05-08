package it.polimi.ingsw.client.controller;
import it.polimi.ingsw.client.view.tui.command.ServerCommandPort;
import it.polimi.ingsw.client.network.ServerProxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController implements ServerCommandPort {

    private final ServerProxy server;
    private final ExecutorService networkExecutor;

    public ServerController(ServerProxy server) {
        this.server = server;
        this.networkExecutor = Executors.newSingleThreadExecutor();
    }

    private void runAsync(Runnable action) {
        networkExecutor.submit(action);
    }

    public void createGame(String nickname, int maxPlayers) {
        runAsync(() -> server.createGame(nickname, maxPlayers));
    }

    public void joinGame(String nickname, String gameId) {
        runAsync(() -> server.joinGame(nickname, gameId));
    }

    public void getAvailableGames() {
        runAsync(server::getAvailableGames);
    }

    public void leaveGame() {
        runAsync(server::leaveGame);
    }

    public void placeTotem(int posIdx) {
        runAsync(() -> server.placeTotem(posIdx));
    }

    public void takeCard(int row, int col) {
        runAsync(() -> server.takeCard(row, col));
    }

    public void skipAction() {
        runAsync(server::skipAction);
    }

    public void getLeaderboard() {
        runAsync(server::getLeaderboard);
    }

    public void disconnect(Runnable completionCallback) {
        networkExecutor.submit(() -> {
            server.disconnect();
            if (completionCallback != null) completionCallback.run();
        });
    }
}

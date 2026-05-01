package it.polimi.ingsw.client.network;
import it.polimi.ingsw.network.client.VirtualServer;
import it.polimi.ingsw.network.messages.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {

    private final VirtualServer server;
    private final ExecutorService networkExecutor;

    public ServerController(VirtualServer server) {
        this.server = server;
        this.networkExecutor = Executors.newSingleThreadExecutor();
    }

    private void sendAsync(ClientMessage message) {
        networkExecutor.submit(() -> {
            server.sendMessage(message);
        });
    }

    public void createGame(String nickname, int maxPlayers) {
        sendAsync(new CreateGameMessage(nickname, maxPlayers));
    }

    public void joinGame(String nickname, String gameId) {
        sendAsync(new JoinGameMessage(nickname, gameId));
    }

    public void getAvailableGames() {
        sendAsync(new GetAvailableGamesMessage());
    }

    public void leaveGame() {
        sendAsync(new LeaveGameMessage());
    }

    public void placeTotem(int posIdx) {
        sendAsync(new PlaceTotemMessage(posIdx));
    }

    public void takeCard(int row, int col) {
        sendAsync(new TakeCardMessage(row, col));
    }

    public void skipAction() {
        sendAsync(new SkipActionMessage());
    }

    public void disconnect(Runnable completionCallback) {
        networkExecutor.submit(() -> {
            server.sendMessage(new DisconnectionMessage());
            server.disconnect();
            if (completionCallback != null) completionCallback.run();
        });
    }
}
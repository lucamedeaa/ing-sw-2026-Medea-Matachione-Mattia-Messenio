package it.polimi.ingsw.client.network;
import it.polimi.ingsw.network.client.VirtualServer;
import it.polimi.ingsw.network.messages.*;

public class ServerController {
    private final VirtualServer server;
    public ServerController(VirtualServer server) { this.server = server; }
    public void createGame(String nickname, int maxPlayers) {
        server.sendMessage(new CreateGameMessage(nickname, maxPlayers));
    }
    public void joinGame(String nickname, String gameId) {
        server.sendMessage(new JoinGameMessage(nickname, gameId));
    }
    public void getAvailableGames() { server.sendMessage(new GetAvailableGamesMessage()); }
    public void leaveGame() { server.sendMessage(new LeaveGameMessage()); }
    public void placeTotem(int posIdx) { server.sendMessage(new PlaceTotemMessage(posIdx)); }
    public void takeCard(int row, int col){ server.sendMessage(new TakeCardMessage(row, col)); }
    public void skipAction() { server.sendMessage(new SkipActionMessage()); }
    public void disconnect() { server.disconnect(); }
}

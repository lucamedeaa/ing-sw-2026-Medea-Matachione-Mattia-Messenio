package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.CreateGameMessage;
import it.polimi.ingsw.network.messages.GetAvailableGamesMessage;
import it.polimi.ingsw.network.messages.JoinGameMessage;
import it.polimi.ingsw.network.messages.LeaveGameMessage;
import it.polimi.ingsw.network.messages.PlaceTotemMessage;
import it.polimi.ingsw.network.messages.SkipActionMessage;
import it.polimi.ingsw.network.messages.TakeCardMessage;

public class SocketServerProxy implements ServerProxy {

    private final SocketServerConnection connection;

    public SocketServerProxy(SocketServerConnection connection) {
        this.connection = connection;
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        connection.sendMessage(new CreateGameMessage(nickname, maxPlayers));
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        connection.sendMessage(new JoinGameMessage(nickname, gameId));
    }

    @Override
    public void getAvailableGames() {
        connection.sendMessage(new GetAvailableGamesMessage());
    }

    @Override
    public void leaveGame() {
        connection.sendMessage(new LeaveGameMessage());
    }

    @Override
    public void placeTotem(int positionIndex) {
        connection.sendMessage(new PlaceTotemMessage(positionIndex));
    }

    @Override
    public void takeCard(int row, int col) {
        connection.sendMessage(new TakeCardMessage(row, col));
    }

    @Override
    public void skipAction() {
        connection.sendMessage(new SkipActionMessage());
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }
}

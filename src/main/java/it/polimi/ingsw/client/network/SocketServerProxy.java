package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.message.client.CreateGameMessage;
import it.polimi.ingsw.common.message.client.GetAvailableGamesMessage;
import it.polimi.ingsw.common.message.client.GetLeaderboardMessage;
import it.polimi.ingsw.common.message.client.JoinGameMessage;
import it.polimi.ingsw.common.message.client.LeaveGameMessage;
import it.polimi.ingsw.common.message.client.PlaceTotemMessage;
import it.polimi.ingsw.common.message.client.SkipActionMessage;
import it.polimi.ingsw.common.message.client.TakeCardMessage;

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
    public void getLeaderboard() {
        connection.sendMessage(new GetLeaderboardMessage());
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }
}

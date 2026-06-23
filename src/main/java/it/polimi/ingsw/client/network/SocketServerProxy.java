package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.message.client.CreateGameMessage;
import it.polimi.ingsw.common.message.client.GetAvailableGamesMessage;
import it.polimi.ingsw.common.message.client.GetLeaderboardMessage;
import it.polimi.ingsw.common.message.client.JoinGameMessage;
import it.polimi.ingsw.common.message.client.LeaveGameMessage;
import it.polimi.ingsw.common.message.client.PlaceTotemMessage;
import it.polimi.ingsw.common.message.client.SkipActionMessage;
import it.polimi.ingsw.common.message.client.TakeCardMessage;

/**
 * Socket implementation of the server proxy used by the client.
 */
public class SocketServerProxy implements ServerProxy {

    private final SocketServerConnection connection;

    /**
     * Creates a proxy that writes command messages to an open socket connection.
     *
     * @param connection active socket connection
     */
    public SocketServerProxy(SocketServerConnection connection) {
        this.connection = connection;
    }

    /** {@inheritDoc} */
    @Override
    public void createGame(String nickname, int maxPlayers) {
        connection.sendMessage(new CreateGameMessage(nickname, maxPlayers));
    }

    /** {@inheritDoc} */
    @Override
    public void joinGame(String nickname, String gameId) {
        connection.sendMessage(new JoinGameMessage(nickname, gameId));
    }

    /** {@inheritDoc} */
    @Override
    public void getAvailableGames() {
        connection.sendMessage(new GetAvailableGamesMessage());
    }

    /** {@inheritDoc} */
    @Override
    public void leaveGame() {
        connection.sendMessage(new LeaveGameMessage());
    }

    /** {@inheritDoc} */
    @Override
    public void placeTotem(int positionIndex) {
        connection.sendMessage(new PlaceTotemMessage(positionIndex));
    }

    /** {@inheritDoc} */
    @Override
    public void takeCard(int row, int col) {
        connection.sendMessage(new TakeCardMessage(row, col));
    }

    /** {@inheritDoc} */
    @Override
    public void skipAction() {
        connection.sendMessage(new SkipActionMessage());
    }

    /** {@inheritDoc} */
    @Override
    public void getLeaderboard() {
        connection.sendMessage(new GetLeaderboardMessage());
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect() {
        connection.disconnect();
    }
}

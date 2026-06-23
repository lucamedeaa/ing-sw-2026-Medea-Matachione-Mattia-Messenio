package it.polimi.ingsw.server.network.state;

import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.lobby.RoomAdmissionResult;
import it.polimi.ingsw.server.lobby.RoomConnectionHandler;
import it.polimi.ingsw.server.model.exception.LobbyActionException;
import it.polimi.ingsw.server.network.ConnectionContext;

/**
 * Connection state that accepts matchmaking and lobby commands.
 */
public class LobbyConnectionState extends UnsupportedConnectionCommands {

    private final ConnectionContext connection;
    private final LobbyController lobbyController;

    /**
     * Creates the lobby state.
     *
     * @param connection connection context
     * @param lobbyController lobby controller handling matchmaking
     */
    public LobbyConnectionState(ConnectionContext connection, LobbyController lobbyController) {
        super(connection);
        this.connection = connection;
        this.lobbyController = lobbyController;
    }

    /** {@inheritDoc} */
    @Override
    public void createGame(String nickname, int maxPlayers) {
        try {
            RoomConnectionHandler room = lobbyController.createGame(nickname, maxPlayers);
            RoomAdmissionResult admissionResult = addPlayerToRoom(room, nickname);
            connection.matchmakingSuccess("Game created. Waiting for other players...");
            // Broadcasts and game start after matchmakingSuccess message.
            admissionResult.afterMatchmakingSuccess();
        } catch (LobbyActionException e) {
            connection.error(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void joinGame(String nickname, String gameId) {
        try {
            RoomConnectionHandler room = lobbyController.joinGame(nickname, gameId);
            RoomAdmissionResult admissionResult = addPlayerToRoom(room, nickname);
            connection.matchmakingSuccess("Joined game successfully. Waiting to start...");
            // Broadcasts and game start after matchmakingSuccess message.
            admissionResult.afterMatchmakingSuccess();
        } catch (LobbyActionException e) {
            connection.error(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void getAvailableGames() {
        connection.availableGames(lobbyController.getAvailableGames());
    }

    /** {@inheritDoc} */
    @Override
    public void leaveGame() {
        try {
            lobbyController.leaveGame(connection.getNickname());
            connection.gameLeftSuccess("You left the lobby.");
        } catch (LobbyActionException e) {
            connection.error(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void handleDisconnection() {
        lobbyController.handleDisconnection(connection.getNickname());
    }

    private RoomAdmissionResult addPlayerToRoom(RoomConnectionHandler room, String nickname) throws LobbyActionException {
        boolean admitted = false;
        try {
            connection.setNickname(nickname);
            RoomAdmissionResult admissionResult = room.addPlayer(nickname, connection);
            admitted = true;
            return admissionResult;
        }
        // We could catch LobbyActionException instead
        finally {
            if (!admitted) {
                connection.setNickname(null);
                lobbyController.releaseNickname(nickname);
            }
        }
    }
}

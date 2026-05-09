package it.polimi.ingsw.server.network.state;

import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.lobby.RoomAdmissionResult;
import it.polimi.ingsw.server.lobby.RoomConnectionHandler;
import it.polimi.ingsw.server.model.exception.LobbyActionException;
import it.polimi.ingsw.server.network.ConnectionContext;

public class LobbyConnectionState extends UnsupportedConnectionCommands {

    private final ConnectionContext connection;
    private final LobbyController lobbyController;

    public LobbyConnectionState(ConnectionContext connection, LobbyController lobbyController) {
        super(connection);
        this.connection = connection;
        this.lobbyController = lobbyController;
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        try {
            RoomAdmissionResult admissionResult;
            // Serialize reservation and room admission with disconnect cleanup.
            admissionResult = connection.withConnectionLock(() -> {
                if (!connection.isActive()) {
                    return null;
                }
                RoomConnectionHandler room = lobbyController.createGame(nickname, maxPlayers);
                return addPlayerToRoom(room, nickname);
            });
            if (admissionResult == null) {
                return;
            }
            connection.matchmakingSuccess("Game created. Waiting for other players...");
            // Broadcasts and game start after matchmakingSuccess message.
            admissionResult.afterMatchmakingSuccess();
        } catch (LobbyActionException e) {
            connection.error(e.getMessage());
        }
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        try {
            RoomAdmissionResult admissionResult;
            // Serialize reservation and room admission with disconnect cleanup.
            admissionResult = connection.withConnectionLock(() -> {
                if (!connection.isActive()) {
                    return null;
                }
                RoomConnectionHandler room = lobbyController.joinGame(nickname, gameId);
                return addPlayerToRoom(room, nickname);
            });
            if (admissionResult == null) {
                return;
            }
            connection.matchmakingSuccess("Joined game successfully. Waiting to start...");
            // Broadcasts and game start after matchmakingSuccess message.
            admissionResult.afterMatchmakingSuccess();
        } catch (LobbyActionException e) {
            connection.error(e.getMessage());
        }
    }

    @Override
    public void getAvailableGames() {
        connection.availableGames(lobbyController.getAvailableGames());
    }

    @Override
    public void leaveGame() {
        try {
            lobbyController.leaveGame(connection.getNickname());
            connection.gameLeftSuccess("You left the lobby.");
        } catch (LobbyActionException e) {
            connection.error(e.getMessage());
        }
    }

    @Override
    public void handleDisconnection() {
        lobbyController.handleDisconnection(connection.getNickname());
    }

    private RoomAdmissionResult addPlayerToRoom(RoomConnectionHandler room, String nickname) throws LobbyActionException {
        boolean admitted = false;
        try {
            // Connection identity must be visible before room membership is added.
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

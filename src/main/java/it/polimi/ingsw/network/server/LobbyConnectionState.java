package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.network.messages.InGameMessage;
import it.polimi.ingsw.network.messages.MatchmakingMessage;
import it.polimi.ingsw.network.visitor.MatchmakingVisitor;
import it.polimi.ingsw.server.RoomAdmissionResult;
import it.polimi.ingsw.server.RoomConnectionHandler;
import it.polimi.ingsw.server.exceptions.RoomFullException;

public class LobbyConnectionState implements ConnectionState {

    private final ConnectionContext connection;
    private final LobbyController lobbyController;
    private final MatchmakingVisitor socketMatchmakingVisitor;

    public LobbyConnectionState(ConnectionContext connection, LobbyController lobbyController) {
        this.connection = connection;
        this.lobbyController = lobbyController;
        this.socketMatchmakingVisitor = new SocketMatchmakingVisitor(this);
    }

    @Override
    public void handle(MatchmakingMessage message) {
        message.accept(socketMatchmakingVisitor);
    }

    @Override
    public void handle(InGameMessage message) {
        connection.error("Not in a game yet.");
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        try {
            RoomAdmissionResult admissionResult;
            // Serialize reservation and room admission with disconnect cleanup.
            synchronized (connection) {
                if (!connection.isActive()) {
                    return;
                }
                RoomConnectionHandler room = lobbyController.createGame(nickname, maxPlayers);
                admissionResult = addPlayerToRoom(room, nickname);
            }
            connection.matchmakingSuccess("Game created. Waiting for other players...");
            admissionResult.afterMatchmakingSuccess();
        } catch (Exception e) {
            connection.error(e.getMessage());
        }
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        try {
            RoomAdmissionResult admissionResult;
            // Serialize reservation and room admission with disconnect cleanup.
            synchronized (connection) {
                if (!connection.isActive()) {
                    return;
                }
                RoomConnectionHandler room = lobbyController.joinGame(nickname, gameId);
                admissionResult = addPlayerToRoom(room, nickname);
            }
            connection.matchmakingSuccess("Joined game successfully. Waiting to start...");
            admissionResult.afterMatchmakingSuccess();
        } catch (Exception e) {
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
        } catch (Exception e) {
            connection.error(e.getMessage());
        }
    }

    @Override
    public void placeTotem(int positionIndex) {
        connection.error("Not in a game yet.");
    }

    @Override
    public void takeCard(int row, int col) {
        connection.error("Not in a game yet.");
    }

    @Override
    public void skipAction() {
        connection.error("Not in a game yet.");
    }

    @Override
    public void handleDisconnection() {
        lobbyController.handleDisconnection(connection.getNickname());
    }

    private RoomAdmissionResult addPlayerToRoom(RoomConnectionHandler room, String nickname) throws RoomFullException {
        try {
            // Connection identity must be visible before room membership is added.
            connection.setNickname(nickname);
            return room.addPlayer(nickname, connection);
        } catch (RoomFullException e) {
            connection.setNickname(null);
            lobbyController.releaseNickname(nickname);
            throw e;
        } catch (RuntimeException e) {
            connection.setNickname(null);
            lobbyController.releaseNickname(nickname);
            throw e;
        }
    }
}

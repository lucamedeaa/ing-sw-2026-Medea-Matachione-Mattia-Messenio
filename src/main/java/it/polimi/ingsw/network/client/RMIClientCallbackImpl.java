package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.LeaderboardSnapshot;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.dto.PlayerGameCompletedDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;
import it.polimi.ingsw.network.rmi.RMIClientCallback;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the RMI callback interface.
 * Receives messages from the server and forwards them to the view/model observer.
 */
public class RMIClientCallbackImpl extends UnicastRemoteObject implements RMIClientCallback {

    private static final Logger LOGGER = Logger.getLogger(RMIClientCallbackImpl.class.getName());

    private final ServerNotificationReceiver receiver;

    public RMIClientCallbackImpl(ServerNotificationReceiver receiver) throws RemoteException {
        super();
        this.receiver = receiver;
    }

    @Override
    public void onFullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) throws RemoteException {
        receiver.fullSync(board, players, activePlayer, actions);
    }

    @Override
    public void onDeltaEvent(List<GameEventDTO> events, List<AvailableActionDTO> nextActions, String activePlayer) throws RemoteException {
        receiver.deltaEvent(events, nextActions, activePlayer);
    }

    @Override
    public void onError(String error) throws RemoteException {
        receiver.error(error);
    }

    @Override
    public void onMatchmakingSuccess(String text) throws RemoteException {
        receiver.matchmakingSuccess(text);
    }

    @Override
    public void onAvailableGames(List<GameInfoDTO> games) throws RemoteException {
        receiver.availableGames(games);
    }

    @Override
    public void onGameAborted(String reason) throws RemoteException {
        receiver.gameAborted(reason);
    }

    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) throws RemoteException {
        receiver.roomUpdate(notification, currentPlayers);
    }

    @Override
    public void onGameLeftSuccess(String text) throws RemoteException {
        receiver.gameLeftSuccess(text);
    }

    @Override
    public void onGameCompleted(PlayerGameCompletedDTO completedGame) throws RemoteException {
        receiver.gameCompleted(completedGame);
    }

    @Override
    public void onLeaderboard(LeaderboardSnapshot leaderboard) throws RemoteException {
        receiver.leaderboard(leaderboard);
    }

    void serverDisconnected(String reason) {
        receiver.serverDisconnected(reason);
    }

    public void disconnect() {
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException e) {
            LOGGER.log(Level.FINE, "RMI callback was already unexported.", e);
        }
    }
}

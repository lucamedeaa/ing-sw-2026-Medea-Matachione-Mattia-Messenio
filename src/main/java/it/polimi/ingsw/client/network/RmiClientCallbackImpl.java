package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.network.dto.*;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import it.polimi.ingsw.common.network.dto.event.GameEventDto;
import it.polimi.ingsw.common.rmi.RMIClientCallback;

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
public class RmiClientCallbackImpl extends UnicastRemoteObject implements RMIClientCallback {

    private static final Logger LOGGER = Logger.getLogger(RmiClientCallbackImpl.class.getName());

    private final ServerNotificationReceiver receiver;

    /**
     * Creates a new {@code RmiClientCallbackImpl} instance.
     *
     * @param receiver server notification receiver
     * @throws RemoteException if the operation cannot be completed
     */
    public RmiClientCallbackImpl(ServerNotificationReceiver receiver) throws RemoteException {
        super();
        this.receiver = receiver;
    }

    /** {@inheritDoc} */
    @Override
    public void onFullSync(BoardDto board, List<PlayerDto> players, String activePlayer, List<ActionDto> actions, InitTurnOrderTileDto turnOrderTileDto) throws RemoteException {
        receiver.fullSync(board, players, activePlayer, actions, turnOrderTileDto);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeltaEvent(List<GameEventDto> events, List<ActionDto> nextActions, String activePlayer) throws RemoteException {
        receiver.deltaEvent(events, nextActions, activePlayer);
    }

    /** {@inheritDoc} */
    @Override
    public void onError(String error) throws RemoteException {
        receiver.error(error);
    }

    /** {@inheritDoc} */
    @Override
    public void onMatchmakingSuccess(String text) throws RemoteException {
        receiver.matchmakingSuccess(text);
    }

    /** {@inheritDoc} */
    @Override
    public void onAvailableGames(List<GameInfoDto> games) throws RemoteException {
        receiver.availableGames(games);
    }

    /** {@inheritDoc} */
    @Override
    public void onGameAborted(String reason) throws RemoteException {
        receiver.gameAborted(reason);
    }

    /** {@inheritDoc} */
    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) throws RemoteException {
        receiver.roomUpdate(notification, currentPlayers);
    }

    /** {@inheritDoc} */
    @Override
    public void onGameLeftSuccess(String text) throws RemoteException {
        receiver.gameLeftSuccess(text);
    }

    /** {@inheritDoc} */
    @Override
    public void onGameCompleted(PlayerGameCompletedDto completedGame) throws RemoteException {
        receiver.gameCompleted(completedGame);
    }

    /** {@inheritDoc} */
    @Override
    public void onLeaderboard(LeaderboardSnapshotDto leaderboard) throws RemoteException {
        receiver.leaderboard(leaderboard);
    }

    /**
     * Handles loss of the server connection.
     *
     * @param reason reason text
     */
    public void serverDisconnected(String reason) {
        receiver.serverDisconnected(reason);
    }

    /** Unexports this callback from the RMI runtime. */
    public void disconnect() {
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException e) {
            LOGGER.log(Level.FINE, "RMI callback was already unexported.", e);
        }
    }
}

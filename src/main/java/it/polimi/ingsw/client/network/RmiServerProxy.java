package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.rmi.RMIServerSession;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RMI implementation of the server proxy used by the client.
 */
public class RmiServerProxy implements ServerProxy {

    private static final Logger LOGGER = Logger.getLogger(RmiServerProxy.class.getName());
    private static final String SERVER_DISCONNECTED_REASON = "Unexpected disconnection from the server.";

    private final RMIServerSession serverSession;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final ScheduledExecutorService pinger;
    private final RmiClientCallbackImpl callback;

    /**
     * Creates a proxy backed by an RMI server session and starts the heartbeat task.
     *
     * @param serverSession remote server session
     * @param callback callback object exported by the client
     */
    public RmiServerProxy(RMIServerSession serverSession, RmiClientCallbackImpl callback) {
        this.serverSession = serverSession;
        this.callback = callback;
        this.pinger = Executors.newSingleThreadScheduledExecutor();
        this.pinger.scheduleAtFixedRate(() -> invoke(serverSession::ping), 5, 5, TimeUnit.SECONDS);
    }

    /** {@inheritDoc} */
    @Override
    public void createGame(String nickname, int maxPlayers) {
        invoke(() -> serverSession.createGame(nickname, maxPlayers));
    }

    /** {@inheritDoc} */
    @Override
    public void joinGame(String nickname, String gameId) {
        invoke(() -> serverSession.joinGame(nickname, gameId));
    }

    /** {@inheritDoc} */
    @Override
    public void getAvailableGames() {
        invoke(serverSession::getAvailableGames);
    }

    /** {@inheritDoc} */
    @Override
    public void leaveGame() {
        invoke(serverSession::leaveGame);
    }

    /** {@inheritDoc} */
    @Override
    public void placeTotem(int positionIndex) {
        invoke(() -> serverSession.placeTotem(positionIndex));
    }

    /** {@inheritDoc} */
    @Override
    public void takeCard(int row, int col) {
        invoke(() -> serverSession.takeCard(row, col));
    }

    /** {@inheritDoc} */
    @Override
    public void skipAction() {
        invoke(serverSession::skipAction);
    }

    /** {@inheritDoc} */
    @Override
    public void getLeaderboard() {
        invoke(serverSession::getLeaderboard);
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect() {
        if (active.compareAndSet(true, false)) {
            try {
                serverSession.disconnect();
            } catch (RemoteException e) {
                LOGGER.log(Level.FINE, "Could not notify the RMI server before closing the connection.", e);
            } finally {
                closeConnection();
            }
        }
    }

    private void invoke(RemoteCall call) {
        if (!active.get()) {
            return;
        }
        try {
            call.run();
        } catch (RemoteException e) {
            handleServerDisconnection(e);
        }
    }

    private void handleServerDisconnection(RemoteException cause) {
        if (active.compareAndSet(true, false)) {
            LOGGER.log(Level.INFO, "RMI server connection lost.", cause);
            try {
                callback.serverDisconnected(SERVER_DISCONNECTED_REASON);
            } finally {
                closeConnection();
            }
        }
    }

    private void closeConnection() {
        pinger.shutdownNow();
        callback.disconnect();
    }

    @FunctionalInterface
    private interface RemoteCall {
        void run() throws RemoteException;
    }
}

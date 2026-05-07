package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.rmi.RMIServerSession;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RMIServerProxy implements ServerProxy {

    private final RMIServerSession serverSession;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final ScheduledExecutorService pinger;
    private final RMIClientCallbackImpl callback;

    public RMIServerProxy(RMIServerSession serverSession, RMIClientCallbackImpl callback) {
        this.serverSession = serverSession;
        this.callback = callback;
        this.pinger = Executors.newSingleThreadScheduledExecutor();
        this.pinger.scheduleAtFixedRate(() -> invoke(serverSession::ping), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        invoke(() -> serverSession.createGame(nickname, maxPlayers));
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        invoke(() -> serverSession.joinGame(nickname, gameId));
    }

    @Override
    public void getAvailableGames() {
        invoke(serverSession::getAvailableGames);
    }

    @Override
    public void leaveGame() {
        invoke(serverSession::leaveGame);
    }

    @Override
    public void placeTotem(int positionIndex) {
        invoke(() -> serverSession.placeTotem(positionIndex));
    }

    @Override
    public void takeCard(int row, int col) {
        invoke(() -> serverSession.takeCard(row, col));
    }

    @Override
    public void skipAction() {
        invoke(serverSession::skipAction);
    }

    @Override
    public void getLeaderboard() {
        invoke(serverSession::getLeaderboard);
    }

    @Override
    public void disconnect() {
        if (active.compareAndSet(true, false)) {
            try {
                serverSession.disconnect();
            } catch (RemoteException ignored) {
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
            handleServerDisconnection();
        }
    }

    private void handleServerDisconnection() {
        if (active.compareAndSet(true, false)) {
            closeConnection();
            // TODO notificare UI della disconnessione inaspettata.
        }
    }

    private void closeConnection() {
        if (pinger != null && !pinger.isShutdown()) {
            pinger.shutdownNow();
        }
        if (callback != null) {
            callback.disconnect();
        }
    }

    @FunctionalInterface
    private interface RemoteCall {
        void run() throws RemoteException;
    }
}

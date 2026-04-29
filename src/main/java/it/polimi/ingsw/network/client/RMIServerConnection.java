package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.DisconnectionMessage;
import it.polimi.ingsw.network.messages.PingMessage;
import it.polimi.ingsw.network.rmi.RMIServerSession;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** RMI-based implementation of VirtualServer that forwards client messages to the remote server session. */
public class RMIServerConnection implements VirtualServer {

    private final RMIServerSession serverSession;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final ScheduledExecutorService pinger;
    private final RMIClientCallbackImpl callback;

    /** Constructs the RMI server connection. @param serverSession remote server session */
    public RMIServerConnection(RMIServerSession serverSession, RMIClientCallbackImpl callback) {
        this.serverSession = serverSession;
        this.callback = callback;
        this.pinger = Executors.newSingleThreadScheduledExecutor();
        this.pinger.scheduleAtFixedRate(() -> {
            sendMessage(new PingMessage());
        }, 5, 5, TimeUnit.SECONDS);
    }

    /** Sends a message to the server via RMI, handling disconnection on failure. @param message message to send */
    @Override
    public void sendMessage(ClientMessage message) {
        if (active.get()) {
            try {
                serverSession.sendMessage(message);
            } catch (RemoteException e) {
                handleServerDisconnection("Errore di comunicazione RMI: " + e.getMessage());
            }
        }
    }

    @Override
    public void disconnect() {
        if (active.compareAndSet(true, false)) {
            try {
                serverSession.sendMessage(new DisconnectionMessage());
            } catch (RemoteException ignored) {
            } finally {
                closeConnection();
            }
        }
    }

    private void handleServerDisconnection(String reason) {
        if (active.compareAndSet(true, false)) {
            closeConnection();
            //TODO notificare UI della disconnessione inaspettata con la reason
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
}
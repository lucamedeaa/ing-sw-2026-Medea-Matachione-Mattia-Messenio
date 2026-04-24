package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.DisconnectionMessage;
import it.polimi.ingsw.network.messages.PingMessage;
import it.polimi.ingsw.network.rmi.RMIServerSession;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** RMI-based implementation of VirtualServer that forwards client messages to the remote server session. */
public class RMIServerConnection implements VirtualServer {

    private final RMIServerSession serverSession;
    private volatile boolean active;
    private final ScheduledExecutorService pinger;


    /** Constructs the RMI server connection. @param serverSession remote server session */
    public RMIServerConnection(RMIServerSession serverSession) {
        this.serverSession = serverSession;
        this.active = true;
        this.pinger = Executors.newSingleThreadScheduledExecutor();
        this.pinger.scheduleAtFixedRate(() -> {
            sendMessage(new PingMessage());
        }, 5, 5, TimeUnit.SECONDS);
    }

    /** Sends a message to the server via RMI, handling disconnection on failure. @param message message to send */
    @Override
    public void sendMessage(ClientMessage message) {
        if (!active) return;
        try {
            serverSession.sendMessage(message);
        } catch (RemoteException e) {
            handleServerDisconnection();
        }
    }

    @Override
    public void disconnect() {
        if (!active) return;
        this.sendMessage(new DisconnectionMessage());
        this.active = false;
        if (pinger != null) pinger.shutdownNow();
        System.out.println("[RMI] Disconnessione volontaria effettuata.");
    }

    private void handleServerDisconnection(){
        if (!active) return;
        this.active = false;
        if (pinger != null) pinger.shutdownNow();
        //TODO notificare UI della disconnessione (come nell'altro)
    }
}
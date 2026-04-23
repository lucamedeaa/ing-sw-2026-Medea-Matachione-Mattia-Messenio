package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.rmi.RMIServerSession;

import java.rmi.RemoteException;

/** RMI-based implementation of VirtualServer that forwards client messages to the remote server session. */
public class RMIServerConnection implements VirtualServer {

    private final RMIServerSession serverSession;

    /** Constructs the RMI server connection. @param serverSession remote server session */
    public RMIServerConnection(RMIServerSession serverSession) {
        this.serverSession = serverSession;
    }

    /** Sends a message to the server via RMI, handling disconnection on failure. @param message message to send */
    @Override
    public void sendMessage(ClientMessage message) {
        try {
            serverSession.sendMessage(message);
        } catch (RemoteException e) {
            disconnect();
        }
    }

    /** Handles server disconnection (UI notification logic to be implemented). */
    @Override
    public void disconnect() {
        //TODO: mettere view nel costruttore per notificare la disconnessione del server
        // Logica per notificare la UI della caduta di connessione
    }
}
package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.rmi.RMIServerSession;

import java.rmi.RemoteException;

public class RMIServerConnection implements VirtualServer{

    private final RMIServerSession serverSession;

    public RMIServerConnection(RMIServerSession serverSession) {
        this.serverSession = serverSession;
    }

    @Override
    public void sendMessage(ClientMessage message) {
        try {
            serverSession.sendMessage(message);
        } catch (RemoteException e) {
            disconnect();
        }
    }

    @Override
    public void disconnect() {
        //TODO: mettere view nel costruttore per notificare la disconnessione del server
        // Logica per notificare la UI della caduta di connessione
    }
}
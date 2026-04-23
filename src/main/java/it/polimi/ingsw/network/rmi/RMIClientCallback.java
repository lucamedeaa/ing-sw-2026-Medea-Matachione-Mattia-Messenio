package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.network.messages.ServerMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Remote RMI callback interface used by the server to send messages to the client. */
public interface RMIClientCallback extends Remote {

    /** Delivers a server message to the client. @param message message received from the server @throws RemoteException if communication fails */
    void onMessageReceived(ServerMessage message) throws RemoteException;
}
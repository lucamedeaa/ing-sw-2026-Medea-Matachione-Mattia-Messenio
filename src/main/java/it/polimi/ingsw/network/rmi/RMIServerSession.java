package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.network.messages.ClientMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Remote RMI session interface for communication between client and server. */
public interface RMIServerSession extends Remote {

    /** Sends a message from the client to the server. @param message message to send @throws RemoteException if communication fails */
    void sendMessage(ClientMessage message) throws RemoteException;
}
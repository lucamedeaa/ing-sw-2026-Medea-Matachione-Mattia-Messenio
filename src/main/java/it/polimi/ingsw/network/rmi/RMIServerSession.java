package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.network.messages.ClientMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerSession extends Remote {

    void sendMessage(ClientMessage message) throws RemoteException;

}

package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.network.messages.ServerMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientCallback extends Remote {

    void onMessageReceived(ServerMessage message) throws RemoteException;

}
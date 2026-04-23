package it.polimi.ingsw.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.network.server.ClientConnection;
import it.polimi.ingsw.network.server.RMIClientHandler;

public interface RMIConnectionServer extends Remote {
    RMIServerSession connect(RMIClientCallback clientCallback) throws RemoteException;
}
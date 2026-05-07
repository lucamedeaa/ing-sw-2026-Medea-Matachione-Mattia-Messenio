package it.polimi.ingsw.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIConnectionServer extends Remote {
    RMIServerSession connect(RMIClientCallback clientCallback) throws RemoteException;
}

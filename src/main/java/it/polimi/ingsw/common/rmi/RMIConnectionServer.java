package it.polimi.ingsw.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIConnectionServer extends Remote {
    RMIServerSession connect(RMIClientCallback clientCallback) throws RemoteException;
}

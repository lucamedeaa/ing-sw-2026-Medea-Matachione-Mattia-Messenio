package it.polimi.ingsw.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Defines the contract for rmi connection server. */
public interface RMIConnectionServer extends Remote {
    RMIServerSession connect(RMIClientCallback clientCallback) throws RemoteException;
}

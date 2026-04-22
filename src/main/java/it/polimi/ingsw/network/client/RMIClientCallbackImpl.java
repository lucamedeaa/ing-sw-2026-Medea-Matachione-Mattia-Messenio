package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessageVisitor;
import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.rmi.RMIClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClientCallbackImpl extends UnicastRemoteObject implements RMIClientCallback {

    private final ClientMessageVisitor viewObserver;

    public RMIClientCallbackImpl(ClientMessageVisitor viewObserver) throws RemoteException {
        super();
        this.viewObserver = viewObserver;
    }

    @Override
    public void onMessageReceived(ServerMessage message) throws RemoteException {
        if (viewObserver != null) {
            message.accept(viewObserver);
        }
    }
}

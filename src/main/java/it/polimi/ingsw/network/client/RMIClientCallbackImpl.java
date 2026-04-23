package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ClientMessageVisitor;
import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.rmi.RMIClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/** RMI callback implementation that forwards received server messages to a client-side visitor. */
public class RMIClientCallbackImpl extends UnicastRemoteObject implements RMIClientCallback {

    private final ClientMessageVisitor viewObserver;

    /** Constructs the RMI callback. @param viewObserver visitor handling incoming messages @throws RemoteException if export fails */
    public RMIClientCallbackImpl(ClientMessageVisitor viewObserver) throws RemoteException {
        super();
        this.viewObserver = viewObserver;
    }

    /** Receives a message from the server and dispatches it to the visitor. @param message the received message @throws RemoteException if communication fails */
    @Override
    public void onMessageReceived(ServerMessage message) throws RemoteException {
        if (viewObserver != null) {
            message.accept(viewObserver);
        }
    }
}
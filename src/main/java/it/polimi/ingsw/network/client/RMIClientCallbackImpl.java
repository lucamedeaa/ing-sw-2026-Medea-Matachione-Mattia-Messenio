package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.visitor.ClientMessageVisitor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation of the RMI callback interface.
 * Receives messages from the server and forwards them to the view/model observer.
 */
public class RMIClientCallbackImpl extends UnicastRemoteObject implements RMIClientCallback {

    private final ClientMessageVisitor viewObserver;

    public RMIClientCallbackImpl(ClientMessageVisitor viewObserver) throws RemoteException {
        super();
        this.viewObserver = viewObserver;
    }

    @Override
    public void notifyMessage(ServerMessage message) throws RemoteException {
        // Forward the incoming server message to the client's visitor.
        message.accept(viewObserver);
    }
}
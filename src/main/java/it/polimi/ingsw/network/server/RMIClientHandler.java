package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.view.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/** RMI-based client handler that bridges server messages to the client and client messages to the VirtualView. */
public class RMIClientHandler extends UnicastRemoteObject implements ClientConnection, RMIServerSession {

    private final RMIClientCallback callback;
    private final String nickname;
    private VirtualView virtualView;

    /** Constructs the handler. @param callback client callback for outgoing messages @param nickname client nickname @throws RemoteException if export fails */
    public RMIClientHandler(RMIClientCallback callback, String nickname) throws RemoteException {
        super();
        this.callback = callback;
        this.nickname = nickname;
    }

    /** Sets the VirtualView used to forward incoming client messages. @param virtualView associated virtual view */
    @Override
    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    /** Sends a server message to the client; on failure, handles disconnection. @param message message to send */
    @Override
    public void send(ServerMessage message) {
        try {
            callback.onMessageReceived(message);
        } catch (RemoteException e) {
            if (virtualView != null) {
                virtualView.handleDisconnection(nickname);
            }
        }
    }

    /** Receives a client message via RMI and forwards it to the VirtualView. @param message message received @throws RemoteException if communication fails */
    @Override
    public void sendMessage(ClientMessage message) throws RemoteException {
        if (virtualView != null) {
            virtualView.onMessageReceived(message);
        }
    }

    /** Returns the client's nickname. @return nickname */
    public String getNickname() {
        return nickname;
    }
}
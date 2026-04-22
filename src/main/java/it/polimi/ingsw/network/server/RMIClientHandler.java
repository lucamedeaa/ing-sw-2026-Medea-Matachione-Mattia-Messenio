package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.ClientMessage;
import it.polimi.ingsw.network.messages.ServerMessage;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.view.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClientHandler extends UnicastRemoteObject implements ClientConnection, RMIServerSession {

    private final RMIClientCallback callback;
    private final String nickname;
    private VirtualView virtualView;

    public RMIClientHandler(RMIClientCallback callback, String nickname) throws RemoteException {
        super();
        this.callback = callback;
        this.nickname = nickname;
    }

    @Override
    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    @Override
    public void send(ServerMessage message) {
        try {
            callback.onMessageReceived(message);
        } catch (RemoteException e) {
            // Se scatta un'eccezione qui, significa che il Client si è disconnesso
            if (virtualView != null) {
                // TODO: Notificare la VirtualView Controller della disconnessione
                virtualView.handleDisconnection(nickname);
            }
            //TODO: fare che se era in attesa lo rimuove
        }
    }

    @Override
    public void sendMessage(ClientMessage message) throws RemoteException {
        if (virtualView != null) {
            virtualView.onMessageReceived(message);
        }
    }

    public String getNickname() {
        return nickname;
    }
}

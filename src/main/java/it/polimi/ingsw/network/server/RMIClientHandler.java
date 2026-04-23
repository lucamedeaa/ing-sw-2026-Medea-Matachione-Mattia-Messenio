package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.view.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/** RMI-based client handler that bridges server messages to the client and client messages to the VirtualView. */
public class RMIClientHandler extends UnicastRemoteObject implements ClientConnection, RMIServerSession {

    private final RMIClientCallback callback;
    private MatchmakingState matchmakingState;

    private VirtualView virtualView;
    private String nickname;

    /**
     * Constructs the handler. @param callback client callback for outgoing messages @param nickname client nickname @throws RemoteException if export fails
     */
    public RMIClientHandler(GameManager gameManager, RMIClientCallback callback) throws RemoteException {
        super();
        this.callback = callback;
        this.matchmakingState = new MatchmakingState(this, gameManager);
    }

    /**
     * Sets the VirtualView used to forward incoming client messages. @param virtualView associated virtual view
     */
    @Override
    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    /**
     * Sends a server message to the client; on failure, handles disconnection. @param message message to send
     */
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

    /**
     * Receives a client message via RMI and forwards it to the VirtualView. @param message message received @throws RemoteException if communication fails
     */
    @Override
    public void sendMessage(ClientMessage message) throws RemoteException {
        if (message instanceof MatchmakingMessage mm) {
            if (matchmakingState != null) {
                mm.accept(matchmakingState);
            } else {
                send(new ErrorMessageDTO("Already in game."));
            }
        } else if (message instanceof InGameMessage igm) {
            if (virtualView != null) {
                igm.accept(virtualView);
            } else {
                send(new ErrorMessageDTO("Not in a game yet."));
            }
        }

    }
}
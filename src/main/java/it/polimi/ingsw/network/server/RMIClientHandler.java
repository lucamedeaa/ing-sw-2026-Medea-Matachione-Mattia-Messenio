package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;
import it.polimi.ingsw.view.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** RMI-based client handler that bridges server messages to the client and client messages to the VirtualView. */
public class RMIClientHandler extends UnicastRemoteObject implements ClientConnection, RMIServerSession {

    private final RMIClientCallback callback;
    private MatchmakingState matchmakingState;
    private final GameManager gameManager;
    private volatile boolean active;
    private volatile long lastPingTime;
    private final ScheduledExecutorService timeoutChecker;

    private VirtualView virtualView;
    private String nickname;

    /**
     * Constructs the handler. @param callback client callback for outgoing messages @param nickname client nickname @throws RemoteException if export fails
     */
    public RMIClientHandler(GameManager gameManager, RMIClientCallback callback) throws RemoteException {
        super();
        this.callback = callback;
        this.gameManager = gameManager;
        this.matchmakingState = new MatchmakingState(this, gameManager);
        this.active = true;
        this.lastPingTime = System.currentTimeMillis();
        this.timeoutChecker = Executors.newSingleThreadScheduledExecutor();

        this.timeoutChecker.scheduleAtFixedRate(() -> {
            if (active && (System.currentTimeMillis() - lastPingTime > 10000)) {
                System.err.println("[RMI] Timeout: Il client " + nickname + " non invia ping. Ritenuto morto.");
                handleClientDisconnection();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Sets the VirtualView used to forward incoming client messages. @param virtualView associated virtual view
     */
    @Override
    public synchronized void setVirtualView(VirtualView virtualView) {
        this.matchmakingState = null;
        this.virtualView = virtualView;
    }

    /**
     * Sends a server message to the client; on failure, handles disconnection. @param message message to send
     */
    @Override
    public void send(ServerMessage message) {
        try {
            if (active) callback.onMessageReceived(message);
        } catch (RemoteException e) {
            handleClientDisconnection();
        }
    }

    /**
     * Receives a client message via RMI and forwards it to the VirtualView. @param message message received @throws RemoteException if communication fails
     */
    @Override
    public void sendMessage(ClientMessage message) throws RemoteException {
        this.lastPingTime = System.currentTimeMillis();
        //TODO: usare visitor pure qua
        if (message instanceof PingMessage) {
            return;
        }
        else if (message instanceof DisconnectionMessage){
            handleClientDisconnection();
        }
        else if (message instanceof MatchmakingMessage mm) {
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
        }else{
            send(new ErrorMessageDTO("Unknown message type."));
        }

    }

    private void handleClientDisconnection(){
        if (!active) return;
        this.active = false;
        if (virtualView != null) {
            virtualView.handleDisconnection();
        } else if (nickname != null) {
            GameRoom room = gameManager.getGameRoomByPlayer(nickname);
            if (room != null) {
                room.removePlayer(nickname);
            }
        }
    }
}
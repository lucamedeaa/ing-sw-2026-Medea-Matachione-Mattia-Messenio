package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;
import it.polimi.ingsw.virtualView.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** RMI-based client handler that bridges server messages to the client and client messages to the VirtualView. */
public class RMIClientHandler extends UnicastRemoteObject implements ClientConnection, RMIServerSession {

    private final GameManager gameManager;
    private final RMIClientCallback callback;
    private volatile VirtualView virtualView;
    private volatile MatchmakingState matchmakingState;
    private String nickname;
    private Integer lastMatchPlayerCount;

    private final ScheduledExecutorService timeoutChecker;

    private final AtomicBoolean active = new AtomicBoolean(true);
    private final AtomicLong lastPingTime = new AtomicLong();

    /**
     * Constructs the handler. @param callback client callback for outgoing messages @param nickname client nickname @throws RemoteException if export fails
     */
    public RMIClientHandler(GameManager gameManager, RMIClientCallback callback) throws RemoteException {
        super();
        this.callback = callback;
        this.gameManager = gameManager;
        this.matchmakingState = new MatchmakingState(this, gameManager);
        this.lastPingTime.set(System.currentTimeMillis());
        this.timeoutChecker = Executors.newSingleThreadScheduledExecutor();

        this.timeoutChecker.scheduleAtFixedRate(() -> {
            if (active.get() && (System.currentTimeMillis() - lastPingTime.get() > 10000)) {
                System.err.println("[RMI] Timeout: Il client " + nickname + " non invia ping. Ritenuto morto.");
                handleClientDisconnection();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    /**
     * Sets the VirtualView used to forward incoming client messages. @param virtualView associated virtual view
     */
    @Override
    public void setVirtualView(VirtualView virtualView) {
        this.matchmakingState = null;
        this.virtualView = virtualView;
        //in case vView changed after check in handleDisconnection
        if (!this.active.get()) {
            virtualView.handleDisconnection();
        }
    }

    /**
     * Sends a server message to the client; on failure, handles disconnection. @param message message to send
     */
    @Override
    public void send(ServerMessage message) {
        try {
            if (active.get()) {
                callback.onMessageReceived(message);
            }
        } catch (RemoteException e) {
            System.err.println("[RMI] Disconnection detected on write for: " + nickname);
            handleClientDisconnection();
        }
    }

    /**
     * Receives a client message via RMI and forwards it to the VirtualView. @param message message received @throws RemoteException if communication fails
     */
    @Override
    public void sendMessage(ClientMessage message) throws RemoteException {
        this.lastPingTime.set(System.currentTimeMillis());
        if (message instanceof PingMessage) {
            return;
        } else if (message instanceof DisconnectionMessage) {
            handleClientDisconnection();
        } else if (message instanceof MatchmakingMessage mm) {
            MatchmakingState currentMatchmaking = this.matchmakingState;
            if (currentMatchmaking != null) {
                mm.accept(currentMatchmaking);
            } else {
                send(new ErrorMessageDTO("Already in game."));
            }
        } else if (message instanceof InGameMessage igm) {
            if (virtualView != null) {
                igm.accept(virtualView);
            } else {
                send(new ErrorMessageDTO("Not in a game yet."));
            }
        } else {
            send(new ErrorMessageDTO("Unknown message type."));
        }
    }

    private void handleClientDisconnection() {
        if (!active.compareAndSet(true, false)) {
            return;
        }

        closeConnection();
        VirtualView currentView = this.virtualView;
        String currentNickname = this.nickname;
        if (currentView != null) {
            currentView.handleDisconnection();
        } else if (currentNickname != null) {
            GameRoom room = gameManager.getGameRoomByPlayer(currentNickname);
            if (room != null) {
                try {
                    room.removePlayer(currentNickname);
                } catch (IllegalStateException e) {
                    System.out.println("[RMI] Disconnessione tardiva in lobby per: " + currentNickname);
                }
            }else{
                gameManager.unregisterNickname(currentNickname);
            }
        }
    }

    @Override
    public void returnToLobby(int playerCount) {
        synchronized (this) {
            if (this.nickname != null) {
                gameManager.unregisterNickname(this.nickname);
                this.nickname = null;
            }
            this.virtualView = null;
            this.lastMatchPlayerCount = playerCount;
            this.matchmakingState = new MatchmakingState(this, gameManager);
        }
    }

    private void closeConnection() {
        if (timeoutChecker != null && !timeoutChecker.isShutdown()) {
            timeoutChecker.shutdownNow();
        }
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (java.rmi.NoSuchObjectException e) {
            System.err.println("[RMI] Impossibile eseguire l'unexport dell'oggetto: " + e.getMessage());
        }
    }
}
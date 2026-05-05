package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import it.polimi.ingsw.network.messages.GameInfoDTO;
import it.polimi.ingsw.network.rmi.RMIClientCallback;
import it.polimi.ingsw.network.rmi.RMIServerSession;
import it.polimi.ingsw.server.GameManager;
import it.polimi.ingsw.server.GameRoom;
import it.polimi.ingsw.virtualView.VirtualView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** RMI client handler exposing native RPC methods for client actions. */
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

    @Override
    public void setVirtualView(VirtualView virtualView) {
        this.matchmakingState = null;
        this.virtualView = virtualView;
        if (!this.active.get()) {
            virtualView.handleDisconnection();
        }
    }

    @Override
    public void ping() {
        touch();
    }

    @Override
    public void disconnect() {
        touch();
        handleClientDisconnection();
    }

    @Override
    public void createGame(String nickname, int maxPlayers) {
        MatchmakingState currentMatchmaking = currentMatchmaking();
        if (currentMatchmaking != null) {
            currentMatchmaking.createGame(nickname, maxPlayers);
        }
    }

    @Override
    public void joinGame(String nickname, String gameId) {
        MatchmakingState currentMatchmaking = currentMatchmaking();
        if (currentMatchmaking != null) {
            currentMatchmaking.joinGame(nickname, gameId);
        }
    }

    @Override
    public void getAvailableGames() {
        MatchmakingState currentMatchmaking = currentMatchmaking();
        if (currentMatchmaking != null) {
            currentMatchmaking.getAvailableGames();
        }
    }

    @Override
    public void leaveGame() {
        MatchmakingState currentMatchmaking = currentMatchmaking();
        if (currentMatchmaking != null) {
            currentMatchmaking.leaveGame();
        }
    }

    @Override
    public void placeTotem(int positionIndex) {
        VirtualView currentView = currentVirtualView();
        if (currentView != null) {
            currentView.placeTotem(positionIndex);
        }
    }

    @Override
    public void takeCard(int row, int col) {
        VirtualView currentView = currentVirtualView();
        if (currentView != null) {
            currentView.takeCard(row, col);
        }
    }

    @Override
    public void skipAction() {
        VirtualView currentView = currentVirtualView();
        if (currentView != null) {
            currentView.skipAction();
        }
    }

    @Override
    public void fullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {
        deliver(() -> callback.onFullSync(board, players, activePlayer, actions));
    }

    @Override
    public void deltaEvent(List<GameEventDTO> events, List<AvailableActionDTO> nextActions, String activePlayer) {
        deliver(() -> callback.onDeltaEvent(events, nextActions, activePlayer));
    }

    @Override
    public void error(String error) {
        deliver(() -> callback.onError(error));
    }

    @Override
    public void matchmakingSuccess(String text) {
        deliver(() -> callback.onMatchmakingSuccess(text));
    }

    @Override
    public void availableGames(List<GameInfoDTO> games) {
        deliver(() -> callback.onAvailableGames(games));
    }

    @Override
    public void gameAborted(String reason) {
        deliver(() -> callback.onGameAborted(reason));
    }

    @Override
    public void roomUpdate(String notification, List<String> currentPlayers) {
        deliver(() -> callback.onRoomUpdate(notification, currentPlayers));
    }

    @Override
    public void gameLeftSuccess(String text) {
        deliver(() -> callback.onGameLeftSuccess(text));
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

    private MatchmakingState currentMatchmaking() {
        touch();
        MatchmakingState currentMatchmaking = this.matchmakingState;
        if (currentMatchmaking == null) {
            error("Already in game.");
        }
        return currentMatchmaking;
    }

    private VirtualView currentVirtualView() {
        touch();
        VirtualView currentView = this.virtualView;
        if (currentView == null) {
            error("Not in a game yet.");
        }
        return currentView;
    }

    private void touch() {
        this.lastPingTime.set(System.currentTimeMillis());
    }

    private void deliver(RemoteCall call) {
        if (!active.get()) {
            return;
        }
        try {
            call.run();
        } catch (RemoteException e) {
            System.err.println("[RMI] Disconnection detected on write for: " + nickname);
            handleClientDisconnection();
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
            } else {
                gameManager.unregisterNickname(currentNickname);
            }
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

    @FunctionalInterface
    private interface RemoteCall {
        void run() throws RemoteException;
    }
}

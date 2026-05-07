package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.controller.GameLifecycleCallback;
import it.polimi.ingsw.network.server.RoomClientProxy;
import it.polimi.ingsw.server.exceptions.RoomFullException;
import it.polimi.ingsw.virtualView.VirtualView;
import it.polimi.ingsw.controller.GameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/** Represents a game lobby that manages players, connections, and game lifecycle. */
public class GameRoom implements GameLifecycleCallback, RoomConnectionHandler {

    private final String gameId;
    private final int maxPlayers;
    private final GameManagerInterface gameManager;
    private final Map<String, RoomClientProxy> players;
    private boolean gameStarted;
    private Game game;
    private GameController controller;
    private ExecutorService gameExecutor;

    public GameRoom(String gameId, int maxPlayers, GameManagerInterface gameManager) {
        this.gameId = gameId;
        this.maxPlayers = maxPlayers;
        this.gameManager = gameManager;
        this.players = new ConcurrentHashMap<>();
        this.gameStarted = false;
    }

    /** Adds a player to the room and starts the game if full. */
    public RoomAdmissionResult addPlayer(String nickname, RoomClientProxy connection) throws RoomFullException, IllegalStateException {
        boolean startNow = false;
        // Only room state is mutated under this lock.
        synchronized (this) {
            if (gameStarted) {
                throw new IllegalStateException("Game already started.");
            }
            if (isFull()) {
                throw new RoomFullException("Game is full.");
            }
            players.put(nickname, connection);
            if (isFull()) {
                this.gameStarted = true;
                startNow = true;
            }
        }

        // Run network-visible effects only after matchmaking success.
        RoomAdmissionResult broadcastJoin = new RoomAdmissionResult(
                () -> broadcast("Il giocatore " + nickname + " è entrato.")
        );
        if (startNow) {
            return new RoomAdmissionResult(() -> {
                broadcastJoin.afterMatchmakingSuccess();
                startGame();
            });
        }
        return broadcastJoin;
    }

    /** Initializes game, controller, and virtual views, then starts the game loop. */
    private void startGame() {
        this.game = new Game(getPlayers());
        this.gameExecutor = Executors.newSingleThreadExecutor();
        this.controller = new GameController(game, gameExecutor, this);
        gameExecutor.submit(() -> {
            for (Map.Entry<String, RoomClientProxy> entry : players.entrySet()) {
                String name = entry.getKey();
                RoomClientProxy conn = entry.getValue();
                VirtualView vv = new VirtualView(name, conn);
                conn.transitionToGameState(controller);
                game.addObserver(vv);
            }
            game.start();
        });
    }

    /** Removes a player and handles cleanup or disconnection logic. */
    public void removePlayer(String nickname) throws IllegalStateException {
        boolean roomIsEmpty = false;
        boolean successfullyRemoved = false;
        synchronized (this) {
            if (gameStarted) {
                throw new IllegalStateException("Game already started. Cannot leave now.");
            }

            RoomClientProxy removed = players.remove(nickname);
            if (players.isEmpty()) {
                roomIsEmpty = true;
            } else if (removed != null) {
                successfullyRemoved = true;
            }
        }
        if (successfullyRemoved || roomIsEmpty) {
            gameManager.unregisterNickname(nickname);
        }
        if (roomIsEmpty) {
            gameManager.removeGame(gameId);
        } else if (successfullyRemoved) {
            broadcast("Il giocatore " + nickname + " ha abbandonato la stanza.");
        }
    }

    public void closeRoom(String reason) {
        int finalPlayerCount = this.maxPlayers; // Serve per il DB

        for (RoomClientProxy conn : players.values()) {
            conn.returnToLobby(finalPlayerCount);
        }
        gameManager.removeGame(this.gameId);
    }

    public synchronized boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public synchronized boolean isGameStarted() {
        return gameStarted;
    }

    public synchronized boolean isNicknameTaken(String nickname) {
        return players.containsKey(nickname);
    }

    public String getGameId() {
        return gameId;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public synchronized List<String> getPlayers() {
        return new ArrayList<>(players.keySet());
    }

    public void broadcast(String messageText) {
        List<RoomClientProxy> currentConnections = new ArrayList<>(players.values());
        List<String> currentPlayers = getPlayers();

        for (RoomClientProxy conn : currentConnections) {
            conn.roomUpdate(messageText, currentPlayers);
        }
    }
}

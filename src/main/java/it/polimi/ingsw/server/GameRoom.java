package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.server.ClientConnection;
import it.polimi.ingsw.server.exceptions.RoomFullException;
import it.polimi.ingsw.virtualView.VirtualView;
import it.polimi.ingsw.controller.GameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/** Represents a game lobby that manages players, connections, and game lifecycle. */
public class GameRoom {

    private final String gameId;
    private final int maxPlayers;
    private final GameManager gameManager;
    private final Map<String, ClientConnection> players;
    private boolean gameStarted;
    private Game game;
    private GameController controller;

    public GameRoom(String gameId, int maxPlayers, GameManager gameManager) {
        this.gameId = gameId;
        this.maxPlayers = maxPlayers;
        this.gameManager = gameManager;
        this.players = new ConcurrentHashMap<>();
        this.gameStarted = false;
    }

    /** Adds a player to the room and starts the game if full. */
    public void addPlayer(String nickname, ClientConnection connection) throws RoomFullException, IllegalStateException {
        boolean startNow = false;
        synchronized (this) {
            if (gameStarted) {
                throw new IllegalStateException("Game already started.");
            }
            if (isFull()) {
                throw new RoomFullException("Game is full.");
            }
            connection.setNickname(nickname);
            players.put(nickname, connection);
            if (isFull()) {
                this.gameStarted = true;
                startNow = true;
            }
        }

        if (startNow) {
            startGame();
        }
    }

    /** Initializes game, controller, and virtual views, then starts the game loop. */
    private void startGame() {
        this.game = new Game(getPlayers());
        this.controller = new GameController(game, this);
        //everyone has a virtualview set, if one leaves the game everyone gets notified
        controller.getGameExecutor().submit(() -> {
            for (Map.Entry<String, ClientConnection> entry : players.entrySet()) {
                String name = entry.getKey();
                ClientConnection conn = entry.getValue();
                VirtualView vv = new VirtualView(name, conn, controller);
                conn.setVirtualView(vv);
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

            ClientConnection removed = players.remove(nickname);
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
        //TODO fare un metodo sia per player disconnesso che per partita temrminata correttamente. In uno trall'altro non fai set dei player ultimi
        //GameTerminationMessage terminationMsg = new GameTerminationMessage(reason);
        int finalPlayerCount = this.maxPlayers; // Serve per il DB

        for (ClientConnection conn : players.values()) {
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
        List<ClientConnection> currentConnections = new ArrayList<>(players.values());
        List<String> currentPlayers = getPlayers();

        for (ClientConnection conn : currentConnections) {
            conn.roomUpdate(messageText, currentPlayers);
        }
    }
}

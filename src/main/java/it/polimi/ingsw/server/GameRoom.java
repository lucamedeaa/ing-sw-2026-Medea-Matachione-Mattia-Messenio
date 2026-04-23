package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.server.ClientConnection;
import it.polimi.ingsw.view.VirtualView;
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
    private VirtualView virtualView;

    /** Constructs a game room. @param gameId unique game identifier @param maxPlayers maximum number of players @param gameManager manager handling active games */
    public GameRoom(String gameId, int maxPlayers, GameManager gameManager) {
        this.gameId = gameId;
        this.maxPlayers = maxPlayers;
        this.gameManager = gameManager;
        this.players = new ConcurrentHashMap<>();
        this.gameStarted = false;
    }

    /** Adds a player to the room and starts the game if full. @param nickname player nickname @param connection client connection @throws Exception if room is full, started, or nickname already taken */
    public synchronized void addPlayer(String nickname, ClientConnection connection) throws Exception {
        if (isFull() || gameStarted) {
            throw new Exception("Game full or already started.");
        }
        if (isNicknameTaken(nickname)) {
            throw new Exception("Nickname already in use.");
        }

        players.put(nickname, connection);

        if (isFull()) {
            startGame();
        }
    }

    /** Initializes game, controller, and virtual views, then starts the game loop. */
    private void startGame() {
        this.game = new Game();
        this.controller = new GameController(game);

        for (Map.Entry<String, ClientConnection> entry : players.entrySet()) {
            String name = entry.getKey();
            ClientConnection conn = entry.getValue();
            VirtualView vv = new VirtualView(name, conn, controller);
            conn.setVirtualView(vv);
            game.addObserver(vv);
            game.addPlayer(name);
        }

        this.gameStarted = true;
        new Thread(game::start).start();
    }

    /** Removes a player and handles cleanup or disconnection logic. @param nickname player nickname */
    public synchronized void removePlayer(String nickname) {
        players.remove(nickname);

        if (players.isEmpty()) {
            gameManager.removeGame(gameId);
        } else if (gameStarted && controller != null) {
            controller.handlePlayerDisconnection(nickname);
        }
    }

    /** Checks if the room is full. @return true if max players reached */
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    /** Checks if the game has started. @return true if started */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /** Checks if a nickname is already taken. @param nickname nickname to check @return true if already used */
    public boolean isNicknameTaken(String nickname) {
        return players.containsKey(nickname);
    }

    /** Returns the game identifier. @return game ID */
    public String getGameId() {
        return gameId;
    }

    /** Returns the maximum number of players. @return max players */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /** Returns the list of player nicknames. @return list of players */
    public List<String> getPlayers() {
        return new ArrayList<>(players.keySet());
    }
}
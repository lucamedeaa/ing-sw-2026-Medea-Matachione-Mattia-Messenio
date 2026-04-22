package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.server.ClientConnection;
import it.polimi.ingsw.view.VirtualView;
import it.polimi.ingsw.controller.GameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameRoom {

    private final String gameId;
    private final int maxPlayers;
    private final GameManager gameManager;
    private final Map<String, ClientConnection> players;
    private boolean gameStarted;
    private Game game;
    private GameController controller;
    private VirtualView virtualView;

    public GameRoom(String gameId, int maxPlayers, GameManager gameManager) {
        this.gameId = gameId;
        this.maxPlayers = maxPlayers;
        this.gameManager = gameManager;
        this.players = new ConcurrentHashMap<>();
        this.gameStarted = false;
    }

    public synchronized void addPlayer(String nickname, ClientConnection connection) throws Exception {
        if (isFull() || gameStarted) {
            throw new Exception("Partita piena o già iniziata.");
        }
        if (isNicknameTaken(nickname)) {
            throw new Exception("Nickname già in uso.");
        }

        players.put(nickname, connection);

        if (isFull()) {
            startGame();
        }
    }

    private void startGame() {
        //TODO: riscrivere bene questa usando costruttore del game (addplayer non esiste)
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

    public synchronized void removePlayer(String nickname) {
        players.remove(nickname);

        if (players.isEmpty()) {
            gameManager.removeGame(gameId);
        } else if (gameStarted && controller != null) {
            controller.handlePlayerDisconnection(nickname);
        }
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isNicknameTaken(String nickname) {
        return players.containsKey(nickname);
    }

    public String getGameId() {
        return gameId;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<String> getPlayers() {
        return new ArrayList<>(players.keySet());
    }
}
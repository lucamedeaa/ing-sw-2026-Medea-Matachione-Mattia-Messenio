package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.CompletedGameResult;
import it.polimi.ingsw.model.PlayerGameResult;
import it.polimi.ingsw.controller.GameLifecycleCallback;
import it.polimi.ingsw.network.dto.LeaderboardEntryDTO;
import it.polimi.ingsw.network.dto.PlayerGameCompletedDTO;
import it.polimi.ingsw.network.server.RoomClientProxy;
import it.polimi.ingsw.server.exceptions.RoomFullException;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;
import it.polimi.ingsw.virtualView.VirtualView;
import it.polimi.ingsw.controller.GameController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/** Represents a game lobby that manages players, connections, and game lifecycle. */
public class GameRoom implements GameLifecycleCallback, RoomConnectionHandler {

    private final String gameId;
    private final int maxPlayers;
    private final GameManagerInterface gameManager;
    private final LeaderboardService leaderboardService;
    private final Map<String, RoomClientProxy> players;
    // Room state lock. External callbacks must run after releasing this lock.
    // If both locks are needed, acquire the connection lifecycle lock before this one. (used to create game and add player)
    private final Object roomLock = new Object();
    private boolean gameStarted;
    private ExecutorService gameExecutor;

    public GameRoom(String gameId, int maxPlayers, GameManagerInterface gameManager, LeaderboardService leaderboardService) {
        this.gameId = gameId;
        this.maxPlayers = maxPlayers;
        this.gameManager = gameManager;
        this.leaderboardService = leaderboardService;
        this.players = new HashMap<>();
        this.gameStarted = false;
    }

    /** Adds a player to the room and starts the game if full. */
    public RoomAdmissionResult addPlayer(String nickname, RoomClientProxy connection) throws RoomFullException, IllegalStateException {
        boolean startNow = false;
        // Only room state is mutated under this lock.
        synchronized (roomLock) {
            if (gameStarted) {
                throw new IllegalStateException("Game already started.");
            }
            if (players.size() >= maxPlayers) {
                throw new RoomFullException("Game is full.");
            }
            players.put(nickname, connection);
            if (players.size() >= maxPlayers) {
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
        List<String> playerNames;
        Map<String, RoomClientProxy> connections;
        synchronized (roomLock) {
            playerNames = new ArrayList<>(players.keySet());
            connections = new HashMap<>(players);
        }

        Game game = new Game(playerNames);
        this.gameExecutor = Executors.newSingleThreadExecutor();
        GameController controller = new GameController(game, gameExecutor, this, leaderboardService);
        game.setCompletionHandler(controller);
        gameExecutor.submit(() -> {
            for (Map.Entry<String, RoomClientProxy> entry : connections.entrySet()) {
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
        synchronized (roomLock) {
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

    @Override
    public void closeCompletedRoom(CompletedGameResult completedGame, List<LeaderboardEntryDTO> personalBestEntries) {
        Map<String, RoomClientProxy> connections;
        synchronized (roomLock) {
            connections = new HashMap<>(players);
        }
        int playerCount = completedGame.playerResults().size();
        Map<String, PlayerGameResult> localResults = completedGame.playerResults().stream()
                .collect(Collectors.toMap(PlayerGameResult::nickname, Function.identity()));
        Map<String, LeaderboardEntryDTO> personalBestByNickname = personalBestEntries.stream()
                .collect(Collectors.toMap(LeaderboardEntryDTO::nickname, Function.identity()));

        for (Map.Entry<String, RoomClientProxy> entry : connections.entrySet()) {
            String nickname = entry.getKey();
            RoomClientProxy conn = entry.getValue();
            PlayerGameResult localResult = localResults.get(nickname);
            LeaderboardEntryDTO personalBestEntry = personalBestByNickname.get(nickname);
            if (localResult == null || personalBestEntry == null) {
                conn.error("Unable to build final leaderboard result.");
                continue;
            }

            conn.transitionToAfterGameState(playerCount, leaderboardService);
            conn.gameCompleted(new PlayerGameCompletedDTO(
                    playerCount,
                    localResult.position(),
                    localResult.finalScore(),
                    localResult.remainingFood(),
                    personalBestEntry.position(),
                    personalBestEntry.finalScore(),
                    personalBestEntry.remainingFood()
            ));
        }

        gameManager.removeGame(this.gameId);
        if (gameExecutor != null) {
            gameExecutor.shutdown();
        }
    }

    @Override
    public void closeAbortedRoom(String reason, String excludedNickname) {
        Map<String, RoomClientProxy> connections;
        synchronized (roomLock) {
            connections = new HashMap<>(players);
        }

        for (Map.Entry<String, RoomClientProxy> entry : connections.entrySet()) {
            if (entry.getKey().equals(excludedNickname)) {
                continue;
            }
            RoomClientProxy conn = entry.getValue();
            conn.gameAborted(reason);
            conn.transitionToLobby();
        }
        gameManager.removeGame(this.gameId);
        if (gameExecutor != null) {
            gameExecutor.shutdown();
        }
    }

    public boolean isFull() {
        synchronized (roomLock) {
            return players.size() >= maxPlayers;
        }
    }

    public boolean isGameStarted() {
        synchronized (roomLock) {
            return gameStarted;
        }
    }

    public boolean isNicknameTaken(String nickname) {
        synchronized (roomLock) {
            return players.containsKey(nickname);
        }
    }

    public String getGameId() {
        return gameId;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<String> getPlayers() {
        synchronized (roomLock) {
            return new ArrayList<>(players.keySet());
        }
    }

    public void broadcast(String messageText) {
        List<RoomClientProxy> currentConnections;
        List<String> currentPlayers;
        synchronized (roomLock) {
            currentConnections = new ArrayList<>(players.values());
            currentPlayers = new ArrayList<>(players.keySet());
        }

        for (RoomClientProxy conn : currentConnections) {
            conn.roomUpdate(messageText, currentPlayers);
        }
    }
}

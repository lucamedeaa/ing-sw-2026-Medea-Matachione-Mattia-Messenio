package it.polimi.ingsw.server.lobby;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.CompletedGameResult;
import it.polimi.ingsw.server.model.PlayerGameResult;
import it.polimi.ingsw.server.controller.GameLifecycleCallback;
import it.polimi.ingsw.common.network.dto.LeaderboardEntryDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.server.network.RoomClientProxy;
import it.polimi.ingsw.server.model.exception.LobbyActionException;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;
import it.polimi.ingsw.server.view.VirtualView;
import it.polimi.ingsw.server.controller.GameController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/** Represents a game lobby that manages players, connections, and game lifecycle. */
public class GameRoom implements GameLifecycleCallback, RoomConnectionHandler {

    private static final Logger LOGGER = Logger.getLogger(GameRoom.class.getName());
    private static final String INTERNAL_ABORT_REASON = "The game was interrupted because of an internal server error.";

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
    public RoomAdmissionResult addPlayer(String nickname, RoomClientProxy connection) throws LobbyActionException {
        boolean startNow = false;
        // Only room state is mutated under this lock.
        synchronized (roomLock) {
            if (gameStarted) {
                throw new LobbyActionException("Game already started.");
            }
            if (players.size() >= maxPlayers) {
                throw new LobbyActionException("Game is full.");
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
            try {
                for (Map.Entry<String, RoomClientProxy> entry : connections.entrySet()) {
                    String name = entry.getKey();
                    RoomClientProxy conn = entry.getValue();
                    VirtualView vv = new VirtualView(name, conn);
                    conn.transitionToGameState(controller);
                    game.addObserver(vv);
                }
                game.start();
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "[ROOM] Unexpected failure while starting game " + gameId, e);
                closeRoomAfterUnexpectedFailure();
            }
        });
    }

    private void closeRoomAfterUnexpectedFailure() {
        try {
            closeAbortedRoom(INTERNAL_ABORT_REASON, null);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[ROOM] Failed to close room " + gameId + " after unexpected failure", e);
        }
    }

    /** Removes a player and handles cleanup or disconnection logic. */
    public void removePlayer(String nickname) throws LobbyActionException {
        boolean roomIsEmpty = false;
        boolean successfullyRemoved = false;
        synchronized (roomLock) {
            if (gameStarted) {
                throw new LobbyActionException("Game already started. Cannot leave now.");
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
    public void closeCompletedRoom(CompletedGameResult completedGame, List<LeaderboardEntryDto> personalBestEntries) {Map<String, RoomClientProxy> connections = snapshotConnections();
        notifyCompletedPlayers(connections, completedGame, personalBestEntries);
        closeRoomResources();
    }

    @Override
    public void closeAbortedRoom(String reason, String excludedNickname) {
        Map<String, RoomClientProxy> connections = snapshotConnections();
        notifyAbortedPlayers(connections, reason, excludedNickname);
        closeRoomResources();
    }

    private Map<String, RoomClientProxy> snapshotConnections() {
        synchronized (roomLock) {
            return new HashMap<>(players);
        }
    }

    private void notifyCompletedPlayers(
            Map<String, RoomClientProxy> connections,
            CompletedGameResult completedGame,
            List<LeaderboardEntryDto> personalBestEntries
    ) {
        int playerCount = completedGame.playerResults().size();
        Map<String, PlayerGameResult> localResults = completedGame.playerResults().stream()
                .collect(Collectors.toMap(PlayerGameResult::nickname, Function.identity()));
        Map<String, LeaderboardEntryDto> personalBestByNickname = personalBestEntries.stream()
                .collect(Collectors.toMap(LeaderboardEntryDto::nickname, Function.identity()));

        for (Map.Entry<String, RoomClientProxy> entry : connections.entrySet()) {
            String nickname = entry.getKey();
            RoomClientProxy conn = entry.getValue();
            PlayerGameResult localResult = localResults.get(nickname);
            LeaderboardEntryDto personalBestEntry = personalBestByNickname.get(nickname);
            if (localResult == null || personalBestEntry == null) {
                conn.error("Unable to build final leaderboard result.");
                continue;
            }

            conn.transitionToAfterGameState(playerCount, leaderboardService);
            conn.gameCompleted(buildCompletedDto(playerCount, localResult, personalBestEntry));
        }
    }

    private void notifyAbortedPlayers(
            Map<String, RoomClientProxy> connections,
            String reason,
            String excludedNickname
    ) {
        for (Map.Entry<String, RoomClientProxy> entry : connections.entrySet()) {
            if (entry.getKey().equals(excludedNickname)) {
                continue;
            }
            RoomClientProxy conn = entry.getValue();
            conn.gameAborted(reason);
            conn.transitionToLobby();
        }
    }

    private PlayerGameCompletedDto buildCompletedDto(
            int playerCount,
            PlayerGameResult localResult,
            LeaderboardEntryDto personalBestEntry
    ) {
        return new PlayerGameCompletedDto(
                playerCount,
                localResult.position(),
                localResult.finalScore(),
                localResult.remainingFood(),
                personalBestEntry.position(),
                personalBestEntry.finalScore(),
                personalBestEntry.remainingFood()
        );
    }

    private void closeRoomResources() {
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

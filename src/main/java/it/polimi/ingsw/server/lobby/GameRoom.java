package it.polimi.ingsw.server.lobby;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.CompletedGameResult;
import it.polimi.ingsw.server.model.PlayerGameResult;
import it.polimi.ingsw.server.controller.GameLifecycleCallback;
import it.polimi.ingsw.common.network.dto.LeaderboardEntryDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.server.network.ConnectionContext;
import it.polimi.ingsw.server.model.exception.LobbyActionException;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;
import it.polimi.ingsw.server.view.VirtualView;
import it.polimi.ingsw.server.controller.GameController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
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
    private final Map<String, ConnectionContext> players;
    private final Object roomLock = new Object();
    private boolean gameStarted;
    private boolean roomClosed;
    private Game game;
    private GameController gameController;
    private ExecutorService gameExecutor;

    /**
     * Creates a new {@code GameRoom} instance.
     *
     * @param gameId game identifier
     * @param maxPlayers maximum number of players
     * @param gameManager game manager
     * @param leaderboardService leaderboard service
     */
    public GameRoom(String gameId, int maxPlayers, GameManagerInterface gameManager, LeaderboardService leaderboardService) {
        this.gameId = gameId;
        this.maxPlayers = maxPlayers;
        this.gameManager = gameManager;
        this.leaderboardService = leaderboardService;
        this.players = new LinkedHashMap<>();
        this.gameStarted = false;
    }

    /** Adds a player to the room and starts the game if full. */
    public RoomAdmissionResult addPlayer(String nickname, ConnectionContext connection) throws LobbyActionException {
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
                prepareGame();
                startNow = true;
            }
        }

        // Run network-visible effects only after matchmaking success.
        RoomAdmissionResult broadcastJoin = new RoomAdmissionResult(
                () -> broadcast("The player " + nickname + " has joined.")
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
        Map<String, ConnectionContext> connections;
        Game gameToStart;
        GameController controller;
        ExecutorService executor;
        synchronized (roomLock) {
            connections = new HashMap<>(players);
            gameToStart = this.game;
            controller = this.gameController;
            executor = this.gameExecutor;
        }

        try {
            executor.submit(() -> {
                if (isRoomClosed()) {
                    return;
                }
                try {
                    for (Map.Entry<String, ConnectionContext> entry : connections.entrySet()) {
                        String name = entry.getKey();
                        ConnectionContext session = entry.getValue();
                        VirtualView vv = new VirtualView(name, session);
                        session.transitionToGameState(controller);
                        gameToStart.addObserver(vv);
                    }
                    gameToStart.start();
                } catch (RuntimeException e) {
                    LOGGER.log(Level.SEVERE, "[ROOM] Unexpected failure while starting game " + gameId, e);
                    closeAbortedRoom(INTERNAL_ABORT_REASON, null);
                }
            });
        } catch (RejectedExecutionException e) {
            LOGGER.log(Level.FINE, "[ROOM] Dropped start task for closed game " + gameId, e);
        }
    }

    private boolean isRoomClosed() {
        synchronized (roomLock) {
            return roomClosed;
        }
    }

    private boolean isRoomAlreadyClosedOrMarkClosed() {
        synchronized (roomLock) {
            if (roomClosed) {
                return true;
            }
            roomClosed = true;
            return false;
        }
    }

    private void prepareGame() {
        List<String> playerNames = new ArrayList<>(players.keySet());
        this.game = new Game(playerNames);
        this.gameExecutor = Executors.newSingleThreadExecutor();
        this.gameController = new GameController(game, gameExecutor, this, leaderboardService);
        this.game.setCompletionHandler(gameController);
        this.gameStarted = true;
    }

    /** Removes a player and handles cleanup or disconnection logic. */
    public void removePlayer(String nickname) {
        boolean roomIsEmpty = false;
        boolean successfullyRemoved = false;
        boolean gameAlreadyStarted = false;
        GameController controller = null;
        synchronized (roomLock) {
            if (gameStarted) {
                players.remove(nickname);
                gameAlreadyStarted = true;
                controller = gameController;
            } else {
                ConnectionContext removed = players.remove(nickname);
                if (players.isEmpty()) {
                    roomIsEmpty = true;
                } else if (removed != null) {
                    successfullyRemoved = true;
                }
            }
        }
        if (gameAlreadyStarted) {
            gameManager.unregisterNickname(nickname);
            controller.handlePlayerDisconnection(nickname);
            return;
        }
        if (successfullyRemoved || roomIsEmpty) {
            gameManager.unregisterNickname(nickname);
        }
        if (roomIsEmpty) {
            gameManager.removeGame(gameId);
        } else if (successfullyRemoved) {
            broadcast("The player " + nickname + " left the room.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void closeCompletedRoom(CompletedGameResult completedGame, List<LeaderboardEntryDto> personalBestEntries) {
        if (isRoomAlreadyClosedOrMarkClosed()) {
            return;
        }
        Map<String, ConnectionContext> connections = snapshotConnections();
        notifyCompletedPlayers(connections, completedGame, personalBestEntries);
        closeRoomResources();
    }

    /** {@inheritDoc} */
    @Override
    public void closeAbortedRoom(String reason, String excludedNickname) {
        if (isRoomAlreadyClosedOrMarkClosed()) {
            return;
        }
        Map<String, ConnectionContext> connections = snapshotConnections();
        notifyAbortedPlayers(connections, reason, excludedNickname);
        closeRoomResources();
    }

    private Map<String, ConnectionContext> snapshotConnections() {
        synchronized (roomLock) {
            return new HashMap<>(players);
        }
    }

    private void notifyCompletedPlayers(
            Map<String, ConnectionContext> connections,
            CompletedGameResult completedGame,
            List<LeaderboardEntryDto> personalBestEntries
    ) {
        int playerCount = completedGame.playerResults().size();
        Map<String, PlayerGameResult> localResults = completedGame.playerResults().stream()
                .collect(Collectors.toMap(PlayerGameResult::nickname, Function.identity()));
        Map<String, LeaderboardEntryDto> personalBestByNickname = personalBestEntries.stream()
                .collect(Collectors.toMap(LeaderboardEntryDto::nickname, Function.identity()));

        for (Map.Entry<String, ConnectionContext> entry : connections.entrySet()) {
            String nickname = entry.getKey();
            ConnectionContext session = entry.getValue();
            PlayerGameResult localResult = localResults.get(nickname);
            LeaderboardEntryDto personalBestEntry = personalBestByNickname.get(nickname);
            if (localResult == null || personalBestEntry == null) {
                session.error("Unable to build final leaderboard result.");
                continue;
            }

            session.transitionToAfterGameState(playerCount, leaderboardService);
            session.gameCompleted(buildCompletedDto(playerCount, localResult, personalBestEntry));
        }
    }

    private void notifyAbortedPlayers(
            Map<String, ConnectionContext> connections,
            String reason,
            String excludedNickname
    ) {
        for (Map.Entry<String, ConnectionContext> entry : connections.entrySet()) {
            if (entry.getKey().equals(excludedNickname)) {
                continue;
            }
            ConnectionContext session = entry.getValue();
            session.gameAborted(reason);
            session.transitionToLobby();
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

    /**
     * Returns whether full.
     *
     * @return true if the room is full; false otherwise
     */
    public boolean isFull() {
        synchronized (roomLock) {
            return players.size() >= maxPlayers;
        }
    }

    /**
     * Returns whether game started.
     *
     * @return true if the game has started; false otherwise
     */
    public boolean isGameStarted() {
        synchronized (roomLock) {
            return gameStarted;
        }
    }

    /**
     * Returns whether nickname taken.
     *
     * @param nickname player nickname
     * @return true if the nickname is already in use; false otherwise
     */
    public boolean isNicknameTaken(String nickname) {
        synchronized (roomLock) {
            return players.containsKey(nickname);
        }
    }

    /**
     * Returns the game id.
     *
     * @return the game identifier
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Returns the max players.
     *
     * @return the maximum number of players
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Returns the players.
     *
     * @return the current player nicknames
     */
    public List<String> getPlayers() {
        synchronized (roomLock) {
            return new ArrayList<>(players.keySet());
        }
    }

    /**
     * Broadcasts the broadcast.
     *
     * @param messageText message text
     */
    public void broadcast(String messageText) {
        List<ConnectionContext> currentConnections;
        List<String> currentPlayers;
        synchronized (roomLock) {
            currentConnections = new ArrayList<>(players.values());
            currentPlayers = new ArrayList<>(players.keySet());
        }

        for (ConnectionContext session : currentConnections) {
            session.roomUpdate(messageText, currentPlayers);
        }
    }
}

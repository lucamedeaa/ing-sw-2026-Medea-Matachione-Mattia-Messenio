package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.CompletedGameResult;
import it.polimi.ingsw.server.model.GameCompletionHandler;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.common.network.dto.LeaderboardEntryDto;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController implements GameCompletionHandler {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
    private static final String INTERNAL_ABORT_REASON = "The game was interrupted because of an internal server error.";

    private final ModelControllerInterface game;
    private final ExecutorService gameExecutor;
    private final GameLifecycleCallback lifecycleCallback;
    private final LeaderboardService leaderboardService;

    public GameController(
            ModelControllerInterface game,
            ExecutorService gameExecutor,
            GameLifecycleCallback lifecycleCallback,
            LeaderboardService leaderboardService
    ) {
        this.game = game;
        this.gameExecutor = gameExecutor;
        this.lifecycleCallback = lifecycleCallback;
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void onGameCompleted(CompletedGameResult result) {
        List<LeaderboardEntryDto> personalBestEntries = leaderboardService.recordCompletedGame(result);
        lifecycleCallback.closeCompletedRoom(result, personalBestEntries);
    }

    public void handlePlayerDisconnection(String nickname) {
        submitGameTask("player disconnection for " + nickname, () -> {
            if (!game.abort()) {
                return;
            }
            LOGGER.info("[CONTROLLER] Disconnection of " + nickname + ". Game terminated.");
            lifecycleCallback.closeAbortedRoom("The player " + nickname + " left the room.", nickname);
        });
    }

    public void handleTakeCard(String nickname, int row, int col, Consumer<String> onError) {
        submitGameTask("take card for " + nickname, () -> {
            try {
                game.takeCard(nickname, row, col);
                game.commitEvents();
            } catch (InvalidGameActionException e) {
                onError.accept(e.getMessage());
            }
        });
    }

    public void handlePlaceTotem(String nickname, int positionIndex, Consumer<String> onError) {
        submitGameTask("place totem for " + nickname, () -> {
            try {
                game.placeTotem(nickname, positionIndex);
                game.commitEvents();
            } catch (InvalidGameActionException e) {
                onError.accept(e.getMessage());
            }
        });
    }

    public void handleSkipBonus(String nickname, Consumer<String> onError) {
        submitGameTask("skip bonus for " + nickname, () -> {
            try {
                game.skipBonus(nickname);
                game.commitEvents();
            } catch (InvalidGameActionException e) {
                onError.accept(e.getMessage());
            }
        });
    }

    private void submitGameTask(String description, Runnable task) {
        try {
            gameExecutor.submit(() -> {
                try {
                    task.run();
                } catch (RuntimeException e) {
                    LOGGER.log(Level.SEVERE, "[CONTROLLER] Unexpected failure during " + description, e);
                    if (game.abort()) {
                        lifecycleCallback.closeAbortedRoom(INTERNAL_ABORT_REASON, null);
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            LOGGER.log(Level.FINE, "[CONTROLLER] Dropped game task after game shutdown: " + description, e);
        }
    }
}

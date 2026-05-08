package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.CompletedGameResult;
import it.polimi.ingsw.model.GameCompletionHandler;
import it.polimi.ingsw.model.exceptions.InvalidGameActionException;
import it.polimi.ingsw.network.dto.LeaderboardEntryDTO;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

import java.util.List;
import java.util.concurrent.ExecutorService;
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
        List<LeaderboardEntryDTO> personalBestEntries = leaderboardService.recordCompletedGame(result);
        lifecycleCallback.closeCompletedRoom(result, personalBestEntries);
    }

    public void handlePlayerDisconnection(String nickname) {
        submitGameTask("player disconnection for " + nickname, () -> {
            if (!game.abort()) {
                return;
            }
            LOGGER.info("[CONTROLLER] Disconnection of " + nickname + ". Game terminated.");
            lifecycleCallback.closeAbortedRoom("Il giocatore " + nickname + " ha abbandonato la partita.", nickname);
        });
    }

    public void handleTakeCard(String nickname, int row, int col, java.util.function.Consumer<String> onError) {
        submitGameTask("take card for " + nickname, () -> {
            try {
                game.takeCard(nickname, row, col);
                game.commitEvents();
            } catch (InvalidGameActionException e) {
                onError.accept(e.getMessage());
            }
        });
    }

    public void handlePlaceTotem(String nickname, int positionIndex, java.util.function.Consumer<String> onError) {
        submitGameTask("place totem for " + nickname, () -> {
            try {
                game.placeTotem(nickname, positionIndex);
                game.commitEvents();
            } catch (InvalidGameActionException e) {
                onError.accept(e.getMessage());
            }
        });
    }

    public void handleSkipBonus(String nickname, java.util.function.Consumer<String> onError) {
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
        gameExecutor.submit(() -> {
            try {
                task.run();
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "[CONTROLLER] Unexpected failure during " + description, e);
                abortAfterUnexpectedFailure();
            }
        });
    }

    private void abortAfterUnexpectedFailure() {
        try {
            if (game.abort()) {
                lifecycleCallback.closeAbortedRoom(INTERNAL_ABORT_REASON, null);
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "[CONTROLLER] Failed to abort game after unexpected failure", e);
        }
    }
}

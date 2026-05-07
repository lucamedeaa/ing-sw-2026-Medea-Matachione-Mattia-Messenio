package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.CompletedGameResult;
import it.polimi.ingsw.model.GameCompletionHandler;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.dto.LeaderboardEntryDTO;
import it.polimi.ingsw.server.leaderboard.LeaderboardService;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class GameController implements GameCompletionHandler {

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
        gameExecutor.submit(() -> {
            if (!game.abort()) {
                return;
            }
            System.out.println("[CONTROLLER] Disconnessione di " + nickname + ". Partita terminata.");
            lifecycleCallback.closeAbortedRoom("Partita terminata senza risultati finali.", nickname);
        });
    }

    public void handleTakeCard(String nickname, int row, int col, java.util.function.Consumer<String> onError) {
        gameExecutor.submit(() -> {
            try {
                Player player = game.getPlayerByNickname(nickname);
                game.takeCard(player, row, col);
                game.commitEvents();
            } catch (Exception e) {
                onError.accept(e.getMessage());
            }
        });
    }

    public void handlePlaceTotem(String nickname, int positionIndex, java.util.function.Consumer<String> onError) {
        gameExecutor.submit(() -> {
            try {
                Player player = game.getPlayerByNickname(nickname);
                game.placeTotem(player, positionIndex);
                game.commitEvents();
            } catch (Exception e) {
                onError.accept(e.getMessage());
            }
        });
    }

    public void handleSkipBonus(String nickname, java.util.function.Consumer<String> onError) {
        gameExecutor.submit(() -> {
            try {
                Player player = game.getPlayerByNickname(nickname);
                game.skipBonus(player);
                game.commitEvents();
            } catch (Exception e) {
                onError.accept(e.getMessage());
            }
        });
    }
}

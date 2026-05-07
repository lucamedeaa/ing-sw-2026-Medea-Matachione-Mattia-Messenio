package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.util.concurrent.ExecutorService;

public class GameController {

    private final ModelControllerInterface game;
    private final ExecutorService gameExecutor;
    private final GameLifecycleCallback lifecycleCallback;

    public GameController(
            ModelControllerInterface game,
            ExecutorService gameExecutor,
            GameLifecycleCallback lifecycleCallback
    ) {
        this.game = game;
        this.gameExecutor = gameExecutor;
        this.lifecycleCallback = lifecycleCallback;
    }
    public void handlePlayerDisconnection(String nickname) {
        gameExecutor.submit(() -> {
            if (game.isEnded()) return;
            System.out.println("[CONTROLLER] Disconnessione di " + nickname + ". Partita terminata.");
            game.setEnded();
            //TODO aggiungere la reason per la disconnessione
            checkGameStateAndHandleEnd();
        });
    }

    public void handleTakeCard(String nickname, int row, int col, java.util.function.Consumer<String> onError) {
        gameExecutor.submit(() -> {
            try {
                if (game.isEnded()) return;
                Player player = game.getPlayerByNickname(nickname);
                game.takeCard(player, row, col);
                game.commitEvents();
                checkGameStateAndHandleEnd();
            } catch (Exception e) {
                onError.accept(e.getMessage());
            }
        });
    }

    public void handlePlaceTotem(String nickname, int positionIndex, java.util.function.Consumer<String> onError) {
        gameExecutor.submit(() -> {
            try {
                if (game.isEnded()) return;
                Player player = game.getPlayerByNickname(nickname);
                game.placeTotem(player, positionIndex);
                game.commitEvents();
                checkGameStateAndHandleEnd();
            } catch (Exception e) {
                onError.accept(e.getMessage());
            }
        });
    }

    public void handleSkipBonus(String nickname, java.util.function.Consumer<String> onError) {
        gameExecutor.submit(() -> {
            try {
                if (game.isEnded()) return;
                Player player = game.getPlayerByNickname(nickname);
                game.skipBonus(player);
                game.commitEvents();
                checkGameStateAndHandleEnd();
            } catch (Exception e) {
                onError.accept(e.getMessage());
            }
        });
    }

    private void checkGameStateAndHandleEnd() {
        //TODO salvare dati nel database quando non è per disconnessione giocatori
        if (game.isEnded()) {
            if (this.lifecycleCallback != null) {
                //TODO aggiustare reason
                this.lifecycleCallback.closeRoom("Partita terminata.");
            }
        }
    }
}

package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController {

    private final ModelControllerInterface game;
    private final ExecutorService gameExecutor;

    public GameController(ModelControllerInterface game) {
        this.game = game;
        this.gameExecutor = Executors.newSingleThreadExecutor();
    }

    public ExecutorService getGameExecutor() {
        return gameExecutor;
    }

    @FunctionalInterface
    private interface GameAction {
        void execute(Player player) throws IllegalArgumentException;
    }

    //TODO: scrivere metodi con try catch ed eccezione da lanciare a virtualview

    private void executeAction(String nickname, GameAction action) {
        gameExecutor.submit(() -> {
            try {
                Player player = game.getPlayerByNickname(nickname);
                action.execute(player);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error" + e.getMessage());
            }
        });
    }

    public void handlePlayerDisconnection(String nickname) {
        gameExecutor.submit(() -> {
            // TODO: far zompare la partita
        });
    }

    public void handleTakeCard(String nickname, int row, int col, java.util.function.Consumer<String> onError) {
        gameExecutor.submit(() -> {
            try {
                Player player = game.getPlayerByNickname(nickname);
                game.takeCard(player, row, col);
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
            } catch (Exception e) {
                onError.accept(e.getMessage());
            }
        });
    }
}

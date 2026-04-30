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

    public void handleTakeCard(String nickname, int row, int col) {
        executeAction(nickname, player -> game.takeCard(player, row, col));
    }

    public void handlePlaceTotem(String nickname, int positionIndex) {
        executeAction(nickname, player -> game.placeTotem(player, positionIndex));
    }

    public void handleSkipBonus(String nickname) {
        executeAction(nickname, player -> game.skipBonus(player));
    }
}

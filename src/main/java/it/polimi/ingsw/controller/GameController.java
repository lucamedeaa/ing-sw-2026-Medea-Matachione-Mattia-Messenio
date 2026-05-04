package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.GameRoom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController {

    private final ModelControllerInterface game;
    private final ExecutorService gameExecutor;
    private final GameRoom gameRoom;

    public GameController(ModelControllerInterface game, GameRoom gameRoom) {
        this.game = game;
        this.gameRoom = gameRoom;
        this.gameExecutor = Executors.newSingleThreadExecutor();
    }

    public ExecutorService getGameExecutor() {
        return gameExecutor;
    }


    //TODO: scrivere metodi con try catch ed eccezione da lanciare a virtualview

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
            if (this.gameRoom != null) {
                //TODO aggiustare reason
                this.gameRoom.closeRoom("Partita terminata.");
            }
        }
    }
}

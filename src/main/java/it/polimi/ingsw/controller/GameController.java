package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;

public class GameController {

    private final Game game;

    public GameController(Game game) {
        //TODO: creare interfaccia per il Game
        this.game = game;
    }

    //TODO: scrivere metodi con try catch ed eccezione da lanciare a virtualview

    public void handlePlayerDisconnection(String nickname) {
        // TODO: far zompare la partita
    }

    public void handleTakeCard(String nickname, int row, int col) {
        synchronized (game) {
            if (!game.getCurrentPlayer().equals(nickname)) {
                throw new IllegalStateException("Non è il tuo turno.");
            }
            // Delega la logica profonda al Model (il quale lancerà eccezioni
            // se le risorse non bastano o la carta non esiste)
            game.takeCard(nickname, row, col);
        }
    }

    public void handlePlaceTotem(String nickname, int positionIndex) {
        synchronized (game) {
            if (!game.getCurrentPlayer().equals(nickname)) {
                throw new IllegalStateException("Non è il tuo turno.");
            }
            game.placeTotem(nickname, positionIndex);
        }
    }

    public void handleSkipBonus(String nickname) {
        synchronized (game) {
            if (!game.getCurrentPlayer().equals(nickname)) {
                throw new IllegalStateException("Non è il tuo turno.");
            }
            game.skipBonus(nickname);
        }
    }
}

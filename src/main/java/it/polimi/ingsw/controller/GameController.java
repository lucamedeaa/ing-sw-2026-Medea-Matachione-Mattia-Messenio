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
}

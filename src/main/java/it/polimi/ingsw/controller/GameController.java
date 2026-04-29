package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

public class GameController {

    private final ModelControllerInterface game;

    public GameController(ModelControllerInterface game) {
        //TODO: creare interfaccia per il Game
        this.game = game;
    }

    //TODO: scrivere metodi con try catch ed eccezione da lanciare a virtualview

    public void handlePlayerDisconnection(String nickname) {
        // TODO: far zompare la partita
    }

    public void handleTakeCard(String nickname, int row, int col) {
        //synchronized (game) {
            try{
                Player player = game.getPlayerByNickname(nickname);
                game.takeCard(player, row, col);
            }catch(IllegalArgumentException e){
                //nickname non presente
                throw new IllegalArgumentException("Error" + e.getMessage());
            }
        //}
    }

    public void handlePlaceTotem(String nickname, int positionIndex) {
        //synchronized (game) {
            try{
                Player player = game.getPlayerByNickname(nickname);
                game.placeTotem(player, positionIndex);
            }catch(IllegalArgumentException e){
                //giocatore non trovato
                throw new IllegalArgumentException("Error" + e.getMessage());
            }
        //}
    }

    public void handleSkipBonus(String nickname) {
        //synchronized (game) {
            try{
                Player player = game.getPlayerByNickname(nickname);
                game.skipBonus(player);
            }catch(IllegalArgumentException e){
                throw new IllegalArgumentException("Error" + e.getMessage());
            }
        //}
    }
}

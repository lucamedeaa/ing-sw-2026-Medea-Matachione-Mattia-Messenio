package it.polimi.ingsw.model.gameState;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.OfferTile;

public abstract class GameState {
    private Game game;
    public void transition(GameState newState){}
    public void start(){}
    public void placeTotem(Player player, OfferTile tile){}
    public void takeCard(Player player, int rowIdx, int cardIdx ){}
}

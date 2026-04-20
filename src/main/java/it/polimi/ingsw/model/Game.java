package it.polimi.ingsw.model;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.OfferTile;
import it.polimi.ingsw.model.gameState.GameState;
import java.util.List;

public class Game {
    private Board board;
    private List<Player> players;
    private int players_size;
    private int currentRound;
    private int currentEra;
    private GameState currentState;

    public Game(){}
    public void setState(GameState state){}
    public void start(){}
    public void placeTotem(Player player, OfferTile tile){}
    public void takeCard(Player player, int rowIdx, int cardIdx){}
    public Player determineWinner(){return null;}
    public void incrementRound(){currentRound++;}
    public void resolveEvents(){}


}

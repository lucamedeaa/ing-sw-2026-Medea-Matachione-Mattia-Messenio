package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.gameState.GameState;
import it.polimi.ingsw.model.gameState.InitState;
import java.util.Comparator;
import java.util.List;

public class Game {

    private Board board;
    private List<Player> players;
    private int playersSize;
    private int currentRound;
    private GameState currentState;



    public Game(List<Player> players) {
        if (players == null || players.size() < 2 || players.size() > 5) {
            throw new IllegalArgumentException("Invalid number of players. Must be between 2 and 5.");
        }
        this.players = players;
        this.playersSize = players.size();
        this.board = new Board(this.playersSize);
        this.currentRound = 1;
    }

    public void start() {
        changeState(new InitState(this));
    }

    public void changeState(GameState newState) {
        this.currentState = newState;
        this.currentState.start();
    }

    public void placeTotem(Player player, int tileIndex) {
        this.currentState.placeTotem(player, tileIndex);
    }

    public void takeCard(Player player, int rowIdx, int cardIdx) {
        this.currentState.takeCard(player, rowIdx, cardIdx);
    }

    public void passTurn(Player player) {
        this.currentState.passTurn(player);
    }

    public void incrementRound() {
        this.currentRound++;
    }

    public Player determineWinner() {
        /*TODO implementare*/
    }

    public Board getBoard() {
        return this.board;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public GameState getCurrentState() {
        return this.currentState;
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

}

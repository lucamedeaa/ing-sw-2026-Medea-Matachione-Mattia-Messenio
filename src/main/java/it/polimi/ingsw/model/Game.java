package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.gameState.GameState;
import it.polimi.ingsw.model.gameState.InitState;
import it.polimi.ingsw.network.dto.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game {

    private Board board;
    private List<Player> players;
    private int playersSize;
    private int currentRound;
    private GameState currentState;
    private List<ModelObserver> observers = new ArrayList<>();



    public Game(List<String> players) {
        if (players == null || players.size() < 2 || players.size() > 5) {
            throw new IllegalArgumentException("Invalid number of players. Must be between 2 and 5.");
        }
        //this.players = players;
        //TODO: usare il costruttore del player e randomizzare il colore usando la stringa
        this.playersSize = players.size();

        this.board = new Board(this.playersSize,  this.players);
        this.currentRound = 1;
    }

    public void start() {
        changeState(new InitState(this));
    }

    public void addObserver(ModelObserver obs) { observers.add(obs); }

    public void notifyObservers(GameEventDTO event) {
        String activePlayer = currentState.getActivePlayerNickname();
        List<AvailableActionDTO> actions = List.of();

        if (activePlayer != null) {
            actions = currentState.getAvailableActions(activePlayer);
        }

        ModelUpdateDTO snapshot = new ModelUpdateDTO(event, activePlayer, actions);

        for (ModelObserver obs : observers) {
            obs.onModelUpdate(snapshot);
        }
    }
    public void notifyFullSync() {
        BoardDTO boardDTO = new BoardDTO(
                board.getRow(0).stream().map(opt -> opt.map(Card::getIDcard).orElse(null)).toList(),
                board.getRow(1).stream().map(opt -> opt.map(Card::getIDcard).orElse(null)).toList(),
                board.getCurrentEraNumber(),
                currentRound
        );

        List<PlayerDTO> playersDTO = players.stream()
                .map(p -> new PlayerDTO(p.getNickname(), p.getFood(), p.getPrestigePoints()))
                .toList();

        String activePlayer = (currentState != null) ? currentState.getActivePlayerNickname() : null;
        List<AvailableActionDTO> actions = List.of();
        if (activePlayer != null) {
            actions = currentState.getAvailableActions(activePlayer);
        }

        for (ModelObserver obs : observers) {
            obs.onFullSync(boardDTO, playersDTO, activePlayer, actions);
        }
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

    public void skipBonus(Player player) {
        this.currentState.skipBonus(player);
    }

    public void incrementRound() {
        this.currentRound++;
    }

    public Player determineWinner() {

        Player winner = players.get(0);

        for (int i = 1; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            int currentPP = currentPlayer.calculateTotalScore();
            int winnerPP = winner.calculateTotalScore();

            if (currentPP > winnerPP) {
                winner = currentPlayer;
            } else if (currentPP == winnerPP) {
                // Tie-breaker: Check Food
                if (currentPlayer.getFood() > winner.getFood()) {
                    winner = currentPlayer;
                }
                // Note: If PP and Food are both equal, the player
                // already stored as "winner" remains the winner.
            }
        }

        return winner;
    }

    public Player getPlayerByNickname(String nickname) {
        return players.stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + nickname));
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

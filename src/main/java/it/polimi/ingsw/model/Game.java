package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.ModelControllerInterface;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.TotemColor;
import it.polimi.ingsw.model.gameState.GameState;
import it.polimi.ingsw.model.gameState.InitState;
import it.polimi.ingsw.model.updates.*;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game implements ModelControllerInterface {

    private Board board;
    private List<Player> players;
    private int playersSize;
    private int currentRound;
    private GameState currentState;
    private List<ModelObserver> observers = new ArrayList<>();

    private final List<GameEvent> pendingEvents = new ArrayList<>();



    public Game(List<String> players) {
        if (players == null || players.size() < 2 || players.size() > 5) {
            throw new IllegalArgumentException("Invalid number of players. Must be between 2 and 5.");
        }
        this.players = new ArrayList<>();
        TotemColor[] availableColors = TotemColor.values();

        for (int i = 0; i < players.size(); i++) {
            this.players.add(new Player(players.get(i), availableColors[i % availableColors.length]));
        }
        this.playersSize = players.size();

        this.board = new Board(this.playersSize,  this.players);
        this.currentRound = 1;
    }

    public void start() {
        changeState(new InitState(this));
    }

    public void addObserver(ModelObserver obs) { observers.add(obs); }

    public void pushEvent(GameEvent event) {
        this.pendingEvents.add(event);
    }
    public void commitEvents() {
        if (pendingEvents.isEmpty()) return;

        String activePlayer = currentState != null ? currentState.getActivePlayerNickname() : null;
        List<AvailableAction> actions = activePlayer != null ? currentState.getAvailableActions(activePlayer) : List.of();

        ModelUpdate snapshot = new ModelUpdate(new ArrayList<>(pendingEvents), activePlayer, actions);
        pendingEvents.clear();

        for (ModelObserver obs : observers) {
            obs.onModelUpdate(snapshot);
        }
    }

    public void notifyFullSync() {
        BoardUpdate boardUpdate = new BoardUpdate(
                board.getRow(0).stream().map(opt -> opt.map(Card::getIDcard).orElse(null)).toList(),
                board.getRow(1).stream().map(opt -> opt.map(Card::getIDcard).orElse(null)).toList(),
                board.getCurrentEraNumber(),
                currentRound
        );

        List<PlayerUpdate> playersUpdates = players.stream()
                .map(p -> new PlayerUpdate(p.getNickname(), p.getFood(), p.getPrestigePoints(), p.getTotemColor(), p.getFoodDiscount()))
                .toList();

        String activePlayer = (currentState != null) ? currentState.getActivePlayerNickname() : null;
        List<AvailableAction> actions = List.of();
        if (activePlayer != null) {
            actions = currentState.getAvailableActions(activePlayer);
        }

        for (ModelObserver obs : observers) {
            obs.onFullSync(boardUpdate, playersUpdates, activePlayer, actions);
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

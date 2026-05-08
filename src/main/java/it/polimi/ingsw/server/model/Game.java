package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.ModelControllerInterface;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.enums.TotemColor;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.state.GameEndedState;
import it.polimi.ingsw.server.model.state.GameState;
import it.polimi.ingsw.server.model.state.InitState;
import it.polimi.ingsw.server.model.update.*;


import java.util.ArrayList;
import java.util.List;

public class Game implements ModelControllerInterface {

    private Board board;
    private List<Player> players;
    private int playersSize;
    private int currentRound;
    private GameState currentState;
    private List<ModelObserver> observers = new ArrayList<>();
    private GameCompletionHandler completionHandler = result -> {};

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

    public void setCompletionHandler(GameCompletionHandler completionHandler) {
        this.completionHandler = completionHandler != null ? completionHandler : result -> {};
    }

    public boolean abort(){
        if (this.currentState.isEnded()) {
            return false;
        }
        this.changeState(new GameEndedState(this));
        return true;
    }

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

    @Override
    public void placeTotem(String nickname, int tileIndex) {
        this.currentState.placeTotem(getPlayerByNickname(nickname), tileIndex);
    }

    @Override
    public void takeCard(String nickname, int rowIdx, int cardIdx) {
        this.currentState.takeCard(getPlayerByNickname(nickname), rowIdx, cardIdx);
    }

    @Override
    public void skipBonus(String nickname) {
        this.currentState.skipBonus(getPlayerByNickname(nickname));
    }

    public void incrementRound() {
        this.currentRound++;
    }

    private Player getPlayerByNickname(String nickname) {
        return players.stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new InvalidGameActionException("Player not found: " + nickname));
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


    public void completeNormally(CompletedGameResult result) {
        if (this.currentState.isEnded()) {
            return;
        }
        this.changeState(new GameEndedState(this));
        completionHandler.onGameCompleted(result);
    }
}

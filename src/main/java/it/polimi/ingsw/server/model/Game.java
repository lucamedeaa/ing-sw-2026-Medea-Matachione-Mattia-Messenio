package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.network.dto.InitTurnOrderTileDto;
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

/**
 * Core game model that owns players, board state, state-machine transitions, and observer notifications.
 */
public class Game implements ModelControllerInterface {

    private final Board board;
    private final List<Player> players;
    private int currentRound;
    private GameState currentState;
    private final List<ModelObserver> observers = new ArrayList<>();
    private GameCompletionHandler completionHandler;

    private final List<GameEvent> pendingEvents = new ArrayList<>();

    /**
     * Creates a game for the provided player nicknames.
     *
     * @param players player nicknames, from 2 to 5
     * @throws IllegalArgumentException if the player count is outside the supported range
     */
    public Game(List<String> players) {
        if (players == null || players.size() < 2 || players.size() > 5) {
            throw new IllegalArgumentException("Invalid number of players. Must be between 2 and 5.");
        }
        this.players = new ArrayList<>();
        TotemColor[] availableColors = TotemColor.values();

        for (int i = 0; i < players.size(); i++) {
            this.players.add(new Player(players.get(i), availableColors[i % availableColors.length]));
        }
        int playersSize = players.size();

        this.board = new Board(playersSize,  this.players);
        this.currentRound = 1;
    }

    /**
     * Starts the game state machine.
     */
    public void start() {
        if (currentState != null) {
            return;
        }
        changeState(new InitState(this));
    }

    /**
     * Registers an observer for model updates.
     *
     * @param obs observer to notify
     */
    public void addObserver(ModelObserver obs) { observers.add(obs); }

    /**
     * Sets the callback invoked after a normal game completion.
     *
     * @param completionHandler completion callback, or null to ignore completions
     */
    public void setCompletionHandler(GameCompletionHandler completionHandler) {
        this.completionHandler = completionHandler;
    }

    /**
     * Aborts the game and moves it to the ended state.
     *
     * @return true if the game was aborted, false if it had already ended
     */
    public boolean abort(){
        if (this.currentState != null && this.currentState.isEnded()) {
            return false;
        }
        this.changeState(new GameEndedState(this));
        return true;
    }

    /**
     * Adds an event to the pending update batch.
     *
     * @param event event to publish on the next commit
     */
    public void pushEvent(GameEvent event) {
        this.pendingEvents.add(event);
    }

    /**
     * Publishes pending events to observers with the current active-player state.
     */
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

    /**
     * Sends a complete game snapshot to every observer.
     */
    public void notifyFullSync() {
        BoardUpdate boardUpdate = new BoardUpdate(
                board.getRow(0).stream().map(opt -> opt.map(Card::getIDcard).orElse(null)).toList(),
                board.getRow(1).stream().map(opt -> opt.map(Card::getIDcard).orElse(null)).toList(),
                board.getCurrentEraNumber(),
                currentRound,
                board.getNextDeckEra()
        );

        List<PlayerUpdate> playersUpdates = players.stream()
                .map(p -> new PlayerUpdate(p.getNickname(), p.getFood(), p.getPrestigePoints(), p.getTotemColor(), p.getFoodDiscount(),  p.getSustenanceDiscount()))
                .toList();

        String activePlayer = (currentState != null) ? currentState.getActivePlayerNickname() : null;
        List<AvailableAction> actions = List.of();
        if (activePlayer != null) {
            actions = currentState.getAvailableActions(activePlayer);
        }
        InitTurnOrderTileUpdate turnOrderTile = new InitTurnOrderTileUpdate(board.getCurrentPlayers().stream().map(Player::getNickname).toList());
        for (ModelObserver obs : observers) {
            obs.onFullSync(boardUpdate, playersUpdates, activePlayer, actions, turnOrderTile);
        }
    }

    /**
     * Changes the current game state and starts it immediately.
     *
     * @param newState state to activate
     */
    public void changeState(GameState newState) {
        this.currentState = newState;
        this.currentState.start();
    }

    /** {@inheritDoc} */
    @Override
    public void placeTotem(String nickname, int tileIndex) {
        this.currentState.placeTotem(getPlayerByNickname(nickname), tileIndex);
    }

    /** {@inheritDoc} */
    @Override
    public void takeCard(String nickname, int rowIdx, int cardIdx) {
        this.currentState.takeCard(getPlayerByNickname(nickname), rowIdx, cardIdx);
    }

    /** {@inheritDoc} */
    @Override
    public void skipBonus(String nickname) {
        this.currentState.skipBonus(getPlayerByNickname(nickname));
    }

    /**
     * Advances the round counter by one.
     */
    public void incrementRound() {
        this.currentRound++;
    }

    private Player getPlayerByNickname(String nickname) {
        return players.stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new InvalidGameActionException("Player not found: " + nickname));
    }

    /**
     * Returns the board owned by this game.
     *
     * @return game board
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Returns the players in this game.
     *
     * @return mutable player list owned by the model
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Returns the currently active state.
     *
     * @return current game state
     */
    public GameState getCurrentState() {
        return this.currentState;
    }

    /**
     * Returns the current round number.
     *
     * @return current round
     */
    public int getCurrentRound() {
        return this.currentRound;
    }

    /**
     * Completes the game normally and notifies the completion handler.
     *
     * @param result final result to publish
     */
    public void completeNormally(CompletedGameResult result) {
        if (this.currentState.isEnded()) {
            return;
        }
        this.changeState(new GameEndedState(this));
        if (completionHandler != null) {
            completionHandler.onGameCompleted(result);
        }
    }
}

package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Deck;
import it.polimi.ingsw.model.Factory.TileFactory;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Era.EraOneState;
import it.polimi.ingsw.model.board.Era.EraState;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.Factory.DeckFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/** Represents the main game board. It manages card rows, the offer track, turn order, food bonuses, decks, and era progression. */
public class Board {

    private List<Optional<Card>> upperRow;
    private List<Optional<Card>> lowerRow;
    private List<OfferTile> offerTrack;
    private List<Player> currentTotemOrder;
    private List<Integer> foodTurnOrderBonus;
    private List<Player> nextTotemOrder;
    private int playerCount;
    private Deck tribeDeck;
    private Deck[] buildingDecks;
    private EraState currentEraState;

    /** Constructs the board and initializes rows, decks, offer track, turn order, food bonuses, and starting cards. @param playerCount number of players @param players list of players @throws IllegalStateException if the number of players is not supported */
    public Board(int playerCount, List<Player> players) {
        this.playerCount = playerCount;
        this.upperRow = new ArrayList<>();
        this.lowerRow = new ArrayList<>();
        this.currentEraState = new EraOneState();
        this.offerTrack = TileFactory.createOfferTrack(playerCount);
        this.tribeDeck = DeckFactory.buildTribeDeck(playerCount);
        this.buildingDecks = DeckFactory.buildBuildingDecks(playerCount);
        setupInitialTurnOrder(players);
        setupFoodBonuses();
        this.initBottomRow();
        this.initTopRow();
    }


    /** Returns the building deck corresponding to the current era. @return current era building deck */
    private Deck getCurrentBuildingDeck() {
        return buildingDecks[currentEraState.getEraNumber() - 1];
    }

    /** Fills the lower row with initial cards until it reaches the required size. */
    private void initBottomRow() {
        while (lowerRow.size() < (playerCount + 1)) {
            Card cardToAdd = tribeDeck.draw();
            cardToAdd.placeDuringSetupBottom(this);
        }
    }

    /** Fills the upper row with initial cards and adds the first era buildings. */
    private void initTopRow() {
        while (upperRow.size() < (playerCount + 4)) {
            Card cardToAdd = tribeDeck.draw();
            addTopRow(cardToAdd);
        }
        setupNewEraBuildings();
    }

    /** Adds a card to the lower row. @param c card to add */
    public void addBottomRow(Card c) {
        this.lowerRow.add(Optional.of(c));
    }

    /** Adds a card to the upper row. @param c card to add */
    public void addTopRow(Card c) {
        this.upperRow.add(Optional.of(c));
    }

    /** Prepares the board for the next round by updating turn order, resolving lower-row effects, moving cards, and refilling the upper row. @param players list of players */
    public void cleanupForNextRound(List<Player> players) {
        this.currentTotemOrder = new ArrayList<>(this.nextTotemOrder);
        this.nextTotemOrder.clear();
        this.resolveLowerEvents(players);
        this.moveTopToLow();
        this.refillTopRow();
    }

    private void refillTopRow() {
        int charactersToDraw = playerCount + 4;
        List<Card> drawnCharacters = new ArrayList<>();
        boolean eraTransitionTriggered = false;

        for (int i = 0; i < charactersToDraw; i++) {
            if (tribeDeck.isEmpty()) break;
            Card drawn = tribeDeck.draw();
            if (drawn != null) {
                drawnCharacters.add(drawn);
                if (!eraTransitionTriggered && drawn.getEra() > this.currentEraState.getEraNumber()) {
                    eraTransitionTriggered = true;
                }
            }
        }

        if (eraTransitionTriggered) {
            handleEraTransition();
        }

        List<Optional<Card>> newUpperRow = new ArrayList<>();
        // Personaggi a sinistra
        for (Card c : drawnCharacters) {
            newUpperRow.add(Optional.of(c));
        }
        // Building (rimasti/nuovi) a destra
        newUpperRow.addAll(this.upperRow);
        this.upperRow = newUpperRow;
    }

    /** Advances the game to the next era and applies the corresponding setup changes. */
    private void handleEraTransition() {
        this.currentEraState = this.currentEraState.getNextEra();
        this.currentEraState.transitionSetup(this);
    }

    /** Removes all persistent cards from the lower row, keeping only non-persistent ones. */
    public void clearBuildingsFromLowerRow() {
        List<Card> nonBuildings = lowerRow.stream()
                .flatMap(Optional::stream)
                .filter(card -> !card.isPersistent())
                .toList();
        lowerRow.clear();
        nonBuildings.forEach(this::addBottomRow);
    }

    /** Moves all persistent cards from the upper row to the lower row and keeps non-persistent cards in the upper row. */
    public void shiftBuildingsToBottomRow() {
        List<Card> buildingsToMove = upperRow.stream()
                .flatMap(Optional::stream)
                .filter(Card::isPersistent)
                .toList();
        List<Card> remainingUpper = upperRow.stream()
                .flatMap(Optional::stream)
                .filter(card -> !card.isPersistent())
                .toList();
        upperRow.clear();
        remainingUpper.forEach(this::addTopRow);
        buildingsToMove.forEach(this::addBottomRow);
    }

    /** Draws all building cards from the current era deck and places them in the upper row. */
    public void setupNewEraBuildings() {
        Deck currentDeck = getCurrentBuildingDeck();
        while (!currentDeck.isEmpty()) {
            Card buildingCard = currentDeck.draw();
            addTopRow(buildingCard);
        }
    }

    /** Randomly determines the initial turn order and initializes the next-round order list. @param players list of players */
    private void setupInitialTurnOrder(List<Player> players) {
        this.currentTotemOrder = new ArrayList<>(players);
        Collections.shuffle(this.currentTotemOrder);
        this.nextTotemOrder = new ArrayList<>();

        int[] initialFood = {2, 3, 3, 4, 4}; //food bonus in base all'ordine dei totem nel primo round

        for (int i = 0; i < currentTotemOrder.size(); i++) {
            currentTotemOrder.get(i).addFood(initialFood[i]);
        }
    }

    /** Moves non-persistent cards from the upper row to the lower row and keeps persistent cards in place according to board rules. */
    private void moveTopToLow() {
        // flatMap distrugge gli spazi bianchi vuoti, estraendo solo le carte vere
        List<Card> remainingLower = lowerRow.stream()
                .flatMap(Optional::stream)
                .filter(Card::isPersistent)
                .toList();
        List<Card> cardsSlidingDown = upperRow.stream()
                .flatMap(Optional::stream)
                .filter(card -> !card.isPersistent())
                .toList();
        List<Card> remainingUpper = upperRow.stream()
                .flatMap(Optional::stream)
                .filter(Card::isPersistent)
                .toList();

        lowerRow.clear();
        upperRow.clear();

        // Ricostruzione compatta senza spazi vuoti
        cardsSlidingDown.forEach(this::addBottomRow);
        remainingLower.forEach(this::addBottomRow);
        remainingUpper.forEach(this::addTopRow);
    }

    /** Initializes the food bonus or penalty associated with turn order positions. @throws IllegalStateException if the number of players is unsupported */
    private void setupFoodBonuses() {
        this.foodTurnOrderBonus = switch (this.playerCount) {
            case 2 -> new ArrayList<>(List.of(1, -1));
            case 3 -> new ArrayList<>(List.of(1, 0, -1));
            case 4 -> new ArrayList<>(List.of(2, 1, 0, -1));
            case 5 -> new ArrayList<>(List.of(3, 1, 0, 0, -1));
            default -> throw new IllegalStateException("Not available number of players");
        };
    }

    /** Resolves all cards remaining on both rows, ordered by resolution priority and then by era. @param players list of players */
    public void resolveFinalEvents(List<Player> players) {
        Stream.concat(upperRow.stream(), lowerRow.stream())
                .flatMap(Optional::stream)
                .sorted(Comparator.comparingInt(Card::getResolutionPriority)
                        .thenComparingInt(Card::getEra))
                .forEach(card -> card.execute(players));
    }

    /** Resolves all cards currently in the lower row, ordered by resolution priority and then by era. @param players list of players */
    private void resolveLowerEvents(List<Player> players) {
        lowerRow.stream()
                .flatMap(Optional::stream)
                .sorted(Comparator.comparingInt(Card::getResolutionPriority)
                        .thenComparingInt(Card::getEra))
                .forEach(card -> card.execute(players));
    }

    /** Returns the player whose turn is currently active. @return current player @throws IllegalStateException if no players remain in turn order */
    public Player getCurrentPlayer() {
        if (currentTotemOrder.isEmpty()) {
            throw new IllegalStateException("No players available!");
        }
        return currentTotemOrder.get(0);
    }

    /** Removes the current player from the active turn order after their action has been completed. */
    public void consumeCurrentPlayer() {
        if (!currentTotemOrder.isEmpty()) {
            currentTotemOrder.remove(0);
        }
    }

    /** Indicates whether all players have placed their totems for the round. @return true if no players remain in the current turn order, false otherwise */
    public boolean allTotemsPlaced() {
        return currentTotemOrder.isEmpty();
    }

    /** Adds a player to the next round turn order and applies the food bonus or penalty associated with their return position. @param player player returning their totem */
    public void returnTotem(Player player) {
        this.nextTotemOrder.add(player);
        int currentIndex = this.nextTotemOrder.size() - 1;
        int bonus = foodTurnOrderBonus.get(currentIndex);
        if (bonus > 0) {
            bonus += player.getFoodBonus();
        }
        player.addFood(bonus);
    }

    /** Returns the card at the specified position without removing it. @param rowIndex 0 for upper row, any other value for lower row @param colIndex zero-based column index @return the card at the specified position @throws IllegalStateException if the slot is empty */
    public Card peekCard(int rowIndex, int colIndex) {
        List<Optional<Card>> row = (rowIndex == 0) ? upperRow : lowerRow;
        return row.get(colIndex).orElseThrow(() -> new IllegalStateException("Card already taken"));
    }

    /** Removes and returns the card at the specified position. @param rowIndex 0 for upper row, any other value for lower row @param colIndex zero-based column index @return the removed card @throws IllegalStateException if the slot is already empty */
    public Card takeCard(int rowIndex, int colIndex) {
        Card takenCard = peekCard(rowIndex, colIndex);
        List<Optional<Card>> row = (rowIndex == 0) ? upperRow : lowerRow;
        row.set(colIndex, Optional.empty());
        return takenCard;
    }

    /** Places a player's totem on the specified offer tile. @param idx index of the target tile @param player player placing the totem @throws IllegalArgumentException if the index is invalid @throws IllegalStateException if the tile is already occupied */
    public void placeTotem(int idx, Player player) {
        if (idx < 0 || idx >= offerTrack.size()) {
            throw new IllegalArgumentException("Position not valid.");
        }
        OfferTile targetTile = offerTrack.get(idx);
        if (!targetTile.isFree()) {
            throw new IllegalStateException("Already occupied space.");
        }
        targetTile.setOccupyingPlayer(player);
    }

    /** Returns the offer track. @return list of offer tiles */
    public List<OfferTile> getOfferTrack() {
        return this.offerTrack;
    }

    //potenzialmente cambiarlo a direttamente getRowID, invece di dorlero risolvere ogni volta nei gameState (es: RoundEndState)
    public List<Optional<Card>> getRow(int idx){
        return idx == 0 ? this.upperRow : this.lowerRow;
    }

    public int getCurrentEraNumber() {
        return this.currentEraState.getEraNumber();
    }

}
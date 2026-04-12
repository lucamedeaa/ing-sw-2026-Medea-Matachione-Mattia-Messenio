package it.polimi.ingsw.model.board;
import it.polimi.ingsw.model.Deck;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.Factory.DeckFactory;

import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.Optional;

public class Board {
    private static final Map<Integer, String> TILE_LAYOUTS = Map.of(
            2, "BCEF",
            3, "BCDEF",
            4, "BCDEFG",
            5, "ABCDEFG"
    );
    private List<Optional<Card>> upperRow;
    private List<Optional<Card>> lowerRow;
    private List<OfferTile> offerTrack;
    private List<Player> currentTotemOrder;
    private List<Integer> foodTurnOrderBonus;
    private List<Player> nextTotemOrder;
    private int playerCount;
    private Deck tribeDeck;
    private Deck[] buildingDecks;
    private int currentEra;


    public Board(int playerCount){
        this.upperRow = new ArrayList<>();
        this.lowerRow = new ArrayList<>();
        this.playerCount = playerCount;
        this.offerTrack = new ArrayList<>();
        this.currentEra = 1;
    }

    private Deck getCurrentBuildingDeck() {
        return buildingDecks[currentEra - 1];
    }

    public void setupBoard(List<Player> players) {
        this.tribeDeck = DeckFactory.buildTribeDeck(this.playerCount);
        this.buildingDecks = DeckFactory.buildBuildingDecks(this.playerCount);

        setupOfferTrack();
        setupInitialTurnOrder(players);
        setupFoodBonuses();

        this.initBottomRow();
        this.initTopRow();
    }

    private void initBottomRow(){
        while(lowerRow.size() < (playerCount + 1)){
            Card cardToAdd = tribeDeck.draw();
            cardToAdd.placeDuringSetupBottom(this);
        }
    }
    private void initTopRow(){
        //questo un pelo diverso da refillTopRow perché potrebbero gia esserci eventi, mentre sotto peschi sempre players + 4. pensa se codice puo essere unificato
        while(upperRow.size() < (playerCount + 4)){
            Card cardToAdd = tribeDeck.draw();
            addTopRow(cardToAdd);
        }
        //add if se è vuoto?
        setupNewEraBuildings();
    }

    public void addBottomRow(Card c){
        this.lowerRow.add(Optional.of(c));
    }

    public void addTopRow(Card c){
        this.upperRow.add(Optional.of(c));
    }

    public void cleanupForNextRound(List<Player> players) {
        this.currentTotemOrder = new ArrayList<>(this.nextTotemOrder);
        this.nextTotemOrder.clear();
        this.resolveLowerEvents(players);
        this.moveTopToLow();
        this.refillTopRow();
        }

    public void setupOfferTrack() {
        String layout = TILE_LAYOUTS.get(this.playerCount);
        this.offerTrack = layout.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .map(TileTemplate::valueOf)
                .map(TileTemplate::createTile)
                .toList();
    }


    private void refillTopRow(){
        for(int i = 0; i < playerCount + 4; i++){
            if (tribeDeck.isEmpty()) {
                break;
            }
            Card drawn = tribeDeck.draw();
            addTopRow(drawn);
            if (drawn.getEra() > this.currentEra) {
                handleEraTransition();
            }
        }
    }
    private void handleEraTransition() {
        this.currentEra += 1;
        if (this.currentEra == 3) {
            clearBuildingsFromLowerRow();
        }
        if (this.currentEra == 2 || this.currentEra == 3) {
            shiftBuildingsToBottomRow();
            setupNewEraBuildings();
        }
    }

    private void clearBuildingsFromLowerRow() {
        List<Card> nonBuildings = lowerRow.stream()
                .flatMap(Optional::stream)
                .filter(card -> !card.isPersistent())
                .toList();
        lowerRow.clear();
        nonBuildings.forEach(this::addBottomRow);
    }

    private void shiftBuildingsToBottomRow() {
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

    private void setupNewEraBuildings() {

        Deck currentDeck = getCurrentBuildingDeck();
        while (!currentDeck.isEmpty()) {
            Card buildingCard = currentDeck.draw();
            addTopRow(buildingCard);
        }
    }

    private void setupInitialTurnOrder(List<Player> players){
        this.currentTotemOrder = new ArrayList<>(players);
        Collections.shuffle(this.currentTotemOrder);
        this.nextTotemOrder = new ArrayList<>();
    }


    private void moveTopToLow() {
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
        remainingLower.forEach(this::addBottomRow);
        cardsSlidingDown.forEach(this::addBottomRow);
        remainingUpper.forEach(this::addTopRow);
    }

    private void setupFoodBonuses() {
        this.foodTurnOrderBonus = switch (this.playerCount) {
            case 2 -> new ArrayList<>(List.of(1, -1));
            case 3 -> new ArrayList<>(List.of(1, 0, -1));
            case 4 -> new ArrayList<>(List.of(2, 1, 0, -1));
            case 5 -> new ArrayList<>(List.of(3, 1, 0, 0, -1));
            default -> throw new IllegalStateException("Not available number of players");
        };
    }

    public void resolveFinalEvents(List<Player> players) {
        Stream.concat(upperRow.stream(), lowerRow.stream())
                .flatMap(Optional::stream)
                .sorted(Comparator.comparingInt(Card::getResolutionPriority)
                        .thenComparingInt(Card::getEra))
                .forEach(card -> card.execute(players));
    }

    private void resolveLowerEvents(List<Player> players){
        lowerRow.stream()
                .flatMap(Optional::stream)
                .sorted(Comparator.comparingInt(Card::getResolutionPriority)
                        .thenComparingInt(Card::getEra))
                .forEach(card -> card.execute(players));
    }


    public Player getCurrentPlayer() {
        if (currentTotemOrder.isEmpty()) {
            throw new IllegalStateException("No players available!");
        }
        return currentTotemOrder.get(0);
    }


    public void consumeCurrentPlayer() {
        if (!currentTotemOrder.isEmpty()) {
            currentTotemOrder.remove(0);
        }
    }

    public boolean allTotemsPlaced() {
        return currentTotemOrder.isEmpty();
    }

    public void returnTotem(Player player) {
        this.nextTotemOrder.add(player);
        int currentIndex = this.nextTotemOrder.size() - 1;
        int bonus = foodTurnOrderBonus.get(currentIndex);
        if (bonus > 0){
            bonus += player.getFoodBonus();
        }
        //only building 'TurnBonus' returns 1
        player.addFood(bonus);
        //TODO: Conidering what I said about addFood in Player, I'd do this way here
        /*if (bonus > 0) {
        player.addFood(bonus);
    } else if (bonus < 0) {
        int cost = Math.abs(bonus);
        if (player.getFood() >= cost) {
            player.addFood(-cost);
        } else {
            player.payPrestige(2);
        }
    }*/
    }

    public Card peekCard(int rowIndex, int colIndex) {
        List<Optional<Card>> row = (rowIndex == 0) ? upperRow : lowerRow;
        return row.get(colIndex).orElseThrow(() -> new IllegalStateException("Card already taken"));
    }

    public Card takeCard(int rowIndex, int colIndex) {
        Card takenCard = peekCard(rowIndex, colIndex);
        List<Optional<Card>> row = (rowIndex == 0) ? upperRow : lowerRow;
        row.set(colIndex, Optional.empty());
        return takenCard;
    }

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

    //getters
    public List<OfferTile> getOfferTrack(){
        return this.offerTrack;
    }


}


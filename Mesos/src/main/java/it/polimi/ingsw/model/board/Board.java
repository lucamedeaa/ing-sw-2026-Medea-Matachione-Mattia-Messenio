package it.polimi.ingsw.model.board;
import it.polimi.ingsw.model.Deck;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.SpaceBonus;
import it.polimi.ingsw.model.board.OfferTile;
import it.polimi.ingsw.model.cards.events.Event;
import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;
import it.polimi.ingsw.model.cards.events.Sustenance;
import java.util.stream.IntStream;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board {
    private List<Card> upperRow;

    private List<Card> lowerRow;
    //Offer tiles ordered A->G
    private List<OfferTile> offerTrack;

    //Every space has the player who placed the totem or is empty if the space is free
    private List<Optional<Player>> totemSpaces;
    private Deck tribeDeck;
    private Deck buildingDeck;
    private int currentEra;
    private List<SpaceBonus> foodBonuses;
    private List<Integer> foodAmounts;



    public Board(int playerCount){
        this.upperRow = new ArrayList<>();
        this.lowerRow = new ArrayList<>();
        this.offerTrack = new ArrayList<>();
        this.totemSpaces = new ArrayList<>();
        this.foodBonuses = new ArrayList<>();
        this.foodAmounts = new ArrayList<>();
        this.currentEra = 1;
        refreshBoard(playerCount);
    }

    //Initialises/resets the board
    public void refreshBoard(int playerCount){
        setupOfferTrack(playerCount);
        setupTotemSpaces(playerCount);
    }

    private void setupOfferTrack(int playerCount){
        offerTrack.clear();
    }
    private void setupTotemSpaces(int playerCount){
        totemSpaces.clear();
        foodBonuses.clear();

        for(int i = 0; i < playerCount; ++i){
            totemSpaces.add(Optional.empty()); //optional.empty means "no totem here yet"
        }

        setupFoodBonuses(playerCount);
    }


    private void setupFoodBonuses(int playerCount){
        switch(playerCount){
            case 2 -> {
                foodBonuses.addAll(List.of(
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.PAY_FOOD
            ));
                foodAmounts.addAll(List.of(1 , 1));
            }

            case 3 -> {
                foodBonuses.addAll(List.of(
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.NONE,
                    SpaceBonus.PAY_FOOD
            ));
                foodAmounts.addAll(List.of(1 , 0 , 1));
            }
            case 4 -> {
                foodBonuses.addAll(List.of(
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.NONE,
                    SpaceBonus.PAY_FOOD
            ));
                foodAmounts.addAll(List.of(2, 1 , 0 , 1));
            }
            case 5 -> {
                foodBonuses.addAll(List.of(
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.NONE,
                    SpaceBonus.NONE,
                    SpaceBonus.PAY_FOOD
            ));
                foodAmounts.addAll(List.of(3, 1, 0 , 0 , 1));
            }
        }
    }

    //Resolves all event cards currently in the lower row
    //Sustenance is always resolved last
    public void resolveEvents(List<Player> players){
        List<Event> events = lowerRow.stream()
                .filter(card -> card instanceof Event) //keeps only cards whose runtime type is Event
                .map(card -> (Event) card) //casts each card to Event (safe thanks to instanceof)
                .toList();

        events.stream()
                .filter(event -> !(event instanceof Sustenance)) //resolve everything except Sustenance
                .forEach(event -> event.execute(players));

        events.stream()
                .filter(event -> event instanceof Sustenance) //only sustenance
                .forEach(event -> event.execute(players));
    }

    //returns true when every totem slot is occupied => all players have returned their totem to the turn order tile
    public Boolean allTotemsPlaced(){return totemSpaces.stream().noneMatch(slot -> slot.isEmpty());} //true if no elements in the stream satisfies "is empty"

    public void refillTopRow(int playerCount){
        int cardsToAdd = playerCount + 4;
        for(int i= 0; i < cardsToAdd; ++i){
            Card card = tribeDeck.draw();
            if(card != null){
                upperRow.add(card);
            }
        }
    }

    public void moveToBottom(){
        List<Card> toMove = upperRow.stream()
                .filter(card -> !(card instanceof Building)) //select cards != buildings
                .toList();

        upperRow.removeAll(toMove);
        lowerRow.removeAll(toMove);
    }
    public void removeBottomRow(){
        lowerRow.removeIf(card -> !(card instanceof Building));
    }

    //Called at the start of era 2 and 3 => new building cards -> upper row, old ones in the lower row
    public void shiftBuildingsToLow(){
        List<Card> buildings = upperRow.stream()
                .filter(card -> card instanceof Building)
                .toList();

        upperRow.removeAll(buildings);
        lowerRow.addAll(buildings);
    }
    //Called at the start of era 2 and 3
    public void clearBuildingsLow(){
        lowerRow.removeIf(card -> card instanceof Building);
    }

    //Places the player's totem in the first free slot on the turn order tile and returns the spaceBonus associated
public SpaceBonus returnTotem(Player player){
        int firstFreeBox = IntStream.range(0, totemSpaces.size()) //stream of ints. range: size -1
                .filter(i -> totemSpaces.get(i).isEmpty())//keeps only the indices where the slot is empty
                .findFirst() //returns an OptionalInt with the first matching index/empty if no free slot
                //orElseThrow extract the value: if the value exists then orElseThrow extracts it and assign it to firstFreeBox, else throw the exception
                .orElseThrow(() -> new IllegalStateException( //called only if no free slot
                        "No free space in the turn order tile"
                ));

        //Optional.of(player) should "wrap" the player into an Optional -> slot occupied
        totemSpaces.set(firstFreeBox, Optional.of(player)); //!!!CHECK IF IT IS RIGHT!!!
        return foodBonuses.get(firstFreeBox);
}

public int getLastTotemAmount() {
    int lastOccupied = IntStream.range(0, totemSpaces.size())
            .filter(i -> totemSpaces.get(i).isPresent())
            .reduce((first, second) -> second)//keeps only the last index that passed the filter
            .orElseThrow(() -> new IllegalStateException(
                    "No occupied space found in turn order tile"
            ));
    return foodAmounts.get(lastOccupied);
}

public int getFoodAmount(int index) {
    return foodAmounts.get(index);
}

//returns the list of players in the order they placed their totems. => turn order for the next round
public List<Player> getNextTurnOrder(){
        return totemSpaces.stream()
                .filter(slot -> slot.isPresent())//only slots with a player
                //!!! CHECK !!!
                .map(slot -> slot.get()) // optional<player> to the player it contains
                .toList();
}

public void clearTotemSpaces(){
        totemSpaces.replaceAll(space -> Optional.empty());
}

//getters
public List<OfferTile> getOfferTrack()  {return offerTrack;}
public List<Card> getUpperRow()  {return upperRow;}
public List<Card> getLowerRow()  {return lowerRow;}
}

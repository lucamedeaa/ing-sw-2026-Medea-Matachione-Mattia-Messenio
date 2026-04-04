package it.polimi.ingsw.model.board;
import it.polimi.ingsw.model.Deck;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import java.util.List;

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
    private List<spaceBonus> foodBonuses;

    //index 0 = Era 1, 1 = era 2, 2 = era 3
    //When a counter reaches 0 -> transition to the next era
    private List<Integer> counterCardEra;

    public Board(int playerCount){
        this.upperRow = new ArrayList<>();
        this.lowerRow = new ArrayList<>();
        this.offerTrack = new ArrayList<>();
        this.totemSpaces = new ArrayList<>();
        this.counterCardEra = new ArrayList<>();
        this.foodBonuses = new ArrayList<>();
        this.currentEra = 1;
        refreshBoard(playerCount);
    }

    //Initialises/resets the board
    public void refreshBoard(int playerCount){
        setupOfferTrack(playerCount);
        setupTotemSpaces(playerCount);
        setupCounterCardEra(playerCount);
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

    //TODO check real cards to see if it's right

    private void setupFoodBonuses(int playerCount){
        switch(playerCount){
            case 2 -> foodBonuses.addAll(List.of(
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.PAY_FOOD
            ));
            case 3 -> foodBonuses.addAll(List.of(
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.NONE,
                    SpaceBonus.PAY_FOOD
            ));
            case 4 -> foodBonuses.addAll(List.of(
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.NONE,
                    SpaceBonus.PAY_FOOD
            ));
            case 5 -> foodBonuses.addAll(List.of(
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.GAIN_FOOD,
                    SpaceBonus.NONE,
                    SpaceBonus.NONE,
                    SpaceBonus.PAY_FOOD
            ));
        }
    }

    //Check the rulebook
    private void setupCounterCardEra(int playerCount){
        //to do
    }

    //Resolves all event cards currently in the lower row
    //Sustenance is always resolved last
    public void resolveEvents(List<Player> players){
        List<Event> events = lowerRow.stream()
                .filter(card -> card instanceof Event) //keeps only cards whose runtime type is Event
                .map(card -> (Event) card) //casts each card to Event (safe thanks to instanceof)
                .toList();

        events.stream()
                .filter(event ->> !(event instanceof Sustenance)) //resolve everything except Sustenance
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
                updateCounterCardEra(card);
            }
        }
    }
    //Decrements the era counter for the drawn card's era.
    //When the counter for the current era reaches 0, advances currentEra by 1
    private void updateCounterCardEra(Card card){
        int eraIndex = card.getEra() - 1; //because era 1 = index 0, era 2 = index 1, era 3 = index 2
        int remaining = counterCardEra.get(eraIndex);
        counterCardEra.set(eraIndex, remaining - 1);

        if(counterCardEra.get(eraIndex) == 0 && card.getEra() == currentEra){
            currentEra++;
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
        int firstFreeBox = intStream.range(0, totemSpaces.size()) //stream of ints. range: size -1
                .filter(i -> totemSpaces.get(i).isEmpty())//keeps only the indices where the slot is empty
                .findFirst() //returns an OptionalInt with the first matching index/empty if no free slot
                .orElseThrow(() -> new IllegalStateException( //called only if no free slot
                        "No free space in the turn order tile"
                ));

        //Optional.of(player) should "wrap" the player into an Optional -> slot occupied
        totemSpaces.set(firstFreeBox, Optional.of(player)); //!!!CHECK IF IT IS RIGHT!!!
        return foodBonuses.get(firstFreeBox);
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

//getter
public List<OfferTile> getOfferTrack()  {return offerTrack;}
public List<Card> getUpperRow()  {return upperRow;}
public List<Card> getLowerRow()  {return lowerRow;}
public int getCurrentEra()  {return currentEra;}
}

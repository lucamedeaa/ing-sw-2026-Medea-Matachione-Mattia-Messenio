package it.polimi.ingsw.model;
import it.polimi.ingsw.model.cards.Card;
import java.util.List;

public class Deck {
    private List<Card> cards;
    public boolean isEmpty(){
        return cards.isEmpty();
    }
    public Deck() {}
    public Card draw(){return null;}
    public Card randomPick(){return null;}
}

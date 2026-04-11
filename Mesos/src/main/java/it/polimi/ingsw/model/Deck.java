package it.polimi.ingsw.model;
import it.polimi.ingsw.model.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;
    public boolean isEmpty(){
        return cards.isEmpty();
    }
    public int size(){return cards.size();}
    public Deck(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
        Collections.shuffle(cards);
    }
    public Card draw(){
        if(isEmpty()){
            return null;
        }
        return cards.remove(0);
    }

}

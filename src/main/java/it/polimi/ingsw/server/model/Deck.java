package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a deck of cards.
 * <p>
 * Provides basic operations such as shuffling, drawing cards,
 * and checking deck size or emptiness.
 */
public class Deck {
    private List<Card> cards;

    /**
     * Constructs a deck from a list of cards and shuffles it.
     * @param cards list of cards to initialize the deck
     */
    public Deck(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
        //Collections.shuffle(this.cards);
    }

    /**
     * Checks whether the deck is empty.
     * @return true if the deck has no cards, false otherwise
     */
    public boolean isEmpty(){
        return cards.isEmpty();
    }

    /**
     * Returns the number of cards in the deck.
     * @return deck size
     */
    public int size(){
        return cards.size();
    }

    /**
     * Draws and removes the top card of the deck.
     * @return the drawn card, or null if the deck is empty
     */
    public Card draw(){
        if(isEmpty()){
            return null;
        }
        return cards.remove(0);
    }
}
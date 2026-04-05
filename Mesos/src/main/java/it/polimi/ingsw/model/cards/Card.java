package it.polimi.ingsw.model.cards;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public abstract class Card {
    protected int era;
    public void addCard(Player player) {
    };
    public int getEra() {
        return era;
    }
}


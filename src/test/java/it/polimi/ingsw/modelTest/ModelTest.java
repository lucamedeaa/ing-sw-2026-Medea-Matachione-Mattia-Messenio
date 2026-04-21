package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.enums.TotemColor;

public abstract class ModelTest {
    protected Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    protected void give(Player p, DrawableCard card) {
        p.addCard(card);
    }
}

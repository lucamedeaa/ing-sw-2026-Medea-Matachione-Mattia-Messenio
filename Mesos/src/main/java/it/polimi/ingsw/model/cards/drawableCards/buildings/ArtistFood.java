package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

import java.util.List;

public class ArtistFood extends Building {
    public ArtistFood() {
        this.foodCost= 0; // TODO: check price
        this.prestigePoints = 0; // TODO: check points
    }

    @Override
    public void onCavePaintingsEvent(Player owner) {
        int tot = owner.getArtistNumber();
        owner.addFood(tot);
    }
}

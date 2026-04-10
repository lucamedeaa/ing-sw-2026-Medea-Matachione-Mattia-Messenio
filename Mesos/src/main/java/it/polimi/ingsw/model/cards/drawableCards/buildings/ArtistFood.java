package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

import java.util.List;

public class ArtistFood extends Building {
    public ArtistFood(int foodCost, int prestigePoints, int era) {
        super(foodCost, prestigePoints, era);
    }

    @Override
    public void onCavePaintingsEvent(Player owner) {
        int tot = owner.getArtistNumber();
        owner.addFood(tot);
    }
}

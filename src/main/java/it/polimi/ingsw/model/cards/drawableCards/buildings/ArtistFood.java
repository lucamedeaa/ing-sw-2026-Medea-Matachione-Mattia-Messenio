package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.CharacterType;

import java.util.List;

public class ArtistFood extends Building {
    public ArtistFood(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }

    @Override
    public void onCavePaintingsEvent(Player owner) {
        int tot = owner.countCharactersOfType(CharacterType.ARTIST);
        owner.addFood(tot);
    }
}

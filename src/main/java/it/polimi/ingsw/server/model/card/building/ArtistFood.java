package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Building that grants food during the Cave Paintings event based on the number of ARTIST characters owned. */
public class ArtistFood extends Building {

    /** Constructs the ArtistFood building.
     * @param idcard the card identifier */
    public ArtistFood(int idcard) {
        super(idcard);
    }

    /** Grants food equal to the number of ARTIST characters during the Cave Paintings event.
     * @param owner the owning player */
    @Override
    public void onCavePaintingsEvent(Player owner) {
        int tot = owner.countCharactersOfType(CharacterType.ARTIST);
        owner.addFood(tot);
    }
}
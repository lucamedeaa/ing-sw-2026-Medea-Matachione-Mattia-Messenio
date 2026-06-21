package it.polimi.ingsw.server.model.card.character;

import it.polimi.ingsw.common.config.CardRegistry;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Represents a Shaman character card that contributes a fixed number of stars. */
public class Shaman extends Character {
    private final int starsCount;

    /** Constructs a Shaman card.
     * @param idcard the card identifier */
    public Shaman(int idcard) {
        super(idcard);
        this.foodCost = 0;
        this.starsCount = CardRegistry.getCard(idcard).stars();
    }

    /** Returns the number of stars contributed by this card. @return stars count */
    @Override
    public int getStarsNumber(){return starsCount;}

    /** Returns the character type. @return CharacterType.SHAMAN */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.SHAMAN;
    }
}
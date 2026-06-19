package it.polimi.ingsw.server.model.card.character;

import it.polimi.ingsw.server.model.enums.CharacterType;

/** Represents a Collector character card that provides a food discount. */
public class Collector extends Character {

    /** Constructs a Collector card. @param idcard the card identifier */
    public Collector(int idcard) {
        super(idcard);
        this.foodCost = 0;
    }

    /** Returns the character type. @return CharacterType.COLLECTOR */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.COLLECTOR;
    }
}
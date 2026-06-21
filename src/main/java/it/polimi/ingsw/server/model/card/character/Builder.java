package it.polimi.ingsw.server.model.card.character;

import it.polimi.ingsw.common.config.CardRegistry;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.common.config.CardInfo;

/** Represents a Builder character card that provides a food discount and end-game prestige points. */
public class Builder extends Character {
    private final int foodDiscount;
    private final int endGamePrestigePoints;

    /**
     * Constructs a Builder card.
     * @param idcard @param idcard the card identifier
     */
    public Builder(int idcard) {
        super(idcard);
        CardInfo info = CardRegistry.getCard(idcard);
        this.foodDiscount = info.foodDiscount();
        this.endGamePrestigePoints = info.bonusPrestige();
    }

    /** Returns the food discount provided by this card. @return food discount value */
    @Override
    public int getFoodDiscount(){return foodDiscount;}

    /** Returns the end-game prestige points. @param owner the owning player @return prestige points */
    @Override
    public int getFinalPoints(Player owner){return endGamePrestigePoints;}

    /** Returns the character type. @return CharacterType.BUILDER */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.BUILDER;
    }
}

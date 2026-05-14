package it.polimi.ingsw.server.model.card.character;

import it.polimi.ingsw.common.config.CardRegistry;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.enums.CharacterType;

/** Represents a Hunter character card. If it has an icon, it grants food based on the number of HUNTER cards in the tribe when added. */
public class Hunter extends Character {
    private final boolean hasIcon;

    /** Constructs a Hunter card. @param idcard the card identifier @param era the card era @param hasIcon whether the card has the special icon */
    public Hunter(int idcard) {
        super(idcard);
        this.foodCost = 0;
        this.hasIcon = CardRegistry.getCard(idcard).hasIcon(); // Assegnazione diretta boolean
    }

    /** Applies instant effect: if the card has an icon, grants food equal to the number of HUNTER cards in the tribe. @param owner the owning player */
    @Override
    public void onCardAddedInstantEffects(Player owner){
        int cnt = 0;
        if (this.hasIcon){
            for (Card drawableCard : owner.getTribe()){
                if (drawableCard.getCharacter() == CharacterType.HUNTER){
                    cnt++;
                }
            }
            owner.addFood(cnt);
        }
    }

    /** Returns the character type. @return CharacterType.HUNTER */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.HUNTER;
    }
}
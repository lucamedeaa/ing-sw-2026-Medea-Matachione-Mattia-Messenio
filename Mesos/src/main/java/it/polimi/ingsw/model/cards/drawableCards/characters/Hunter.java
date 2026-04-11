package it.polimi.ingsw.model.cards.drawableCards.characters;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.enums.CharacterType;

public class Hunter extends Character{
    private final Boolean hasIcon;
    public Hunter(int era,Boolean hasIcon) {
        this.foodCost=0;
        this.era=era;
        this.hasIcon = hasIcon;
    }

    public Boolean getHasIcon() {
        return hasIcon;
    }
    @Override
    public void onCardAddedToTribe(Player owner, DrawableCard newcard){
        if(newcard instanceof Hunter && ((Hunter) newcard).getHasIcon()){
            owner.addFood(owner.countCharactersOfType(CharacterType.HUNTER));
        }
    }

    @Override
    public CharacterType getCharacter() {
        return CharacterType.HUNTER;
    }
}

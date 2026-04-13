package it.polimi.ingsw.model.cards.drawableCards.characters;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.enums.CharacterType;

public class Hunter extends Character{
    private final boolean hasIcon;
    public Hunter(int era, boolean hasIcon) {
        this.foodCost=0;
        this.era=era;
        this.hasIcon = hasIcon;
    }

    public boolean getHasIcon() {
        return hasIcon;
    }

    @Override
    public void onCardAddedInstantEffects(Player owner){
        int cnt=0;
        if(this.hasIcon){
            for(DrawableCard drawableCard : owner.getTribe()){
                if(drawableCard.getCharacter()==CharacterType.HUNTER){
                    cnt++;
                }
            }
            owner.addFood(cnt);
        }
    }


    @Override
    public CharacterType getCharacter() {
        return CharacterType.HUNTER;
    }
}

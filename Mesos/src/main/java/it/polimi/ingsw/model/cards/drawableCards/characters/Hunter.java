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
    public void onCardAddedInstantEffects(Player owner){
        int cnt=1;
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

package it.polimi.ingsw.model.cards.drawableCards.characters;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.CharacterType;

public class Hunter extends Character{
    private final boolean hasIcon;
    public Hunter(int idcard, int era, boolean hasIcon) {
        super(idcard, era);
        this.foodCost=0;
        this.hasIcon = hasIcon;
    }


    @Override
    public void onCardAddedInstantEffects(Player owner){
        int cnt=0;
        if(this.hasIcon){
            for(Card drawableCard : owner.getTribe()){
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

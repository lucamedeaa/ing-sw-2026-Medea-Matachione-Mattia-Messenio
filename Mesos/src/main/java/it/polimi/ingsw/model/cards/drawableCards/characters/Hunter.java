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
    /*TODO: riscrivere con logica "inversa"*/
    @Override
    public void onCardAddedToTribe(Player player, List<DrawableCard> tribe){
        int cnt=1;
        if(this.hasIcon){
            for(DrawableCard drawableCard : tribe){
                if(drawableCard.getHunterNumber() == 1){
                    cnt++;
                }
            }
            player.addFood(cnt);
    }
        }
    @Override
    public CharacterType getCharacter() {
        return CharacterType.HUNTER;
    }
}

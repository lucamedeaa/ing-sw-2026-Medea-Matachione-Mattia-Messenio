package it.polimi.ingsw.model.cards.drawableCards.buildings;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.drawableCards.characters.Builder;
import it.polimi.ingsw.model.enums.CharacterType;

public class BuilderMastery extends Building {
    public BuilderMastery(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
    }

    @Override
    public int getFinalPoints(Player owner){
        int finalPoint = 0;
        for (Card card : owner.getTribe()){
            if(card.getCharacter().equals(CharacterType.BUILDER){
                finalPoint += card.getFinalPoints(owner); // owner useless
            }
        }
        return finalPoint + prestigePoints; //instead of doubling builder points, it counts them here a second time
    }
}

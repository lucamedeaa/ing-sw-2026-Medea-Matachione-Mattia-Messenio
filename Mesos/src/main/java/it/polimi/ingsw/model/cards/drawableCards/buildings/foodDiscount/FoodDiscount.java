package it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;
import it.polimi.ingsw.model.enums.CharacterType;

public abstract class FoodDiscount extends Building {
    private CharacterType CharType;
    public FoodDiscount(int foodCost, int prestigePoints, int era, CharacterType CharType) {
        super(foodCost, prestigePoints, era);
        this.CharType = CharType;
    }
    @Override
    public int onSustenanceEvent(Player owner) {
        int tot = 0;
        switch (CharType){
            case ARTIST:
                tot = owner.getArtistNumber();
            case COLLECTOR:
                tot = owner.getCollectorNumber();
            case INVENTOR:
                tot = owner.getInventorsNumber();
        }
        return tot;
    }
}

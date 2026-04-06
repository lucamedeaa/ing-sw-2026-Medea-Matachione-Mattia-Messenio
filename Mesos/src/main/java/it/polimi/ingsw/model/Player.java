package it.polimi.ingsw.model;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.enums.InventorIcon;
import it.polimi.ingsw.model.enums.TotemColor;
import java.util.List;
import java.util.Set;

public class Player {
    private String name;
    private int food;
    private List<DrawableCard> tribe;
    private int prestigePoints;
    private TotemColor totemColor;

    public Player(String name, int food, List<DrawableCard> tribe, TotemColor totemColor){}
    public void calculateTotalScore(){}
    public int getBuilderDiscount(){return 0;}
    public int getCollectorNumber(){return 0;}
    public int getHunterNumber(){return 0;}
    public int getShamanNumber(){return 0;}
    public int getStarsNumber(){return 0;}
    public int getArtistNumber(){return 0;}
    public int getInventorsNumber(){return 0;}
    public int getInventorIconsNumber(Set<InventorIcon> icons){return 0;}
    public void addCard(DrawableCard card){}
    public void addFood(int amount){}
    public void payFood(int amount){}
    public void addPrestige(int amount){}
    public void payPrestige(int amount){}


    public List<DrawableCard> getTribe() {return tribe;}

    public int getFood() { return food; }
}

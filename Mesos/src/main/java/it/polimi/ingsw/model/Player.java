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

    public Player(String name, int food, List<DrawableCard> tribe, TotemColor totemColor){
        this.name = name;
        this.food = 0;
        this.prestigePoints = 0;
        this.tribe = new ArrayList<>(tribe);
        this.totemColor = totemColor;
    }

    //getFinalPoints(tribe) is called on every card.
    //Most cards return 0. Builder, Inventor, Artist override it with their own end-game scoring logic
    public void calculateTotalScore(){
        return prestigePoints + tribe.stream()
                .mapToInt(card -> card.getFinalPoints(tribe)) //calls getFinalPoints() on each card and converts the result to a primitive int
                .sum();
    }

    //Only Builder cards return a value greater than 0 from getBuilderNumber() . All other card types return 0, so they don't affect the sum.
    public int getBuilderDiscount(){
        return tribe.stream()
                .mapToInt(card -> card.getDiscount(tribe))
                .sum();
    }

    //Used during the Sustenance event to calculate the food discount (each Collector reduces the food cost by 3).
    public int getCollectorNumber(){
        return tribe.stream()
                .mapToInt(card -> card.getCollectorNumber(tribe))
                .sum();
    }

    //Used during the Hunt event to calculate food gained and PP earned.
    public int getHunterNumber(){
        return tribe.stream()
                .mapToInt(card -> card.getHunterNumber(tribe))
                .sun();
    }

    //Shamans are counted by their star icons, not by card type, so we call getStarsNumber() rather than a dedicated shaman counter.
    public int getShamanNumber(){
        return tribe.stream()
                .mapToInt(card -> card.getShamanNumber(tribe))
                .sum();
    }

    //Used during the Shamanic Ritual event to determine majority/minority.
    public int getStarsNumber(){
        return tribe.stream()
                .mapToInt(card -> card.getStarsNumber(tribe))
                .sum();
    }
    //Used during the Cave Paintings event and for end-of-game scoring (10 PP per every 2 Artists).
    public int getArtistNumber(){
        return tribe.stream()
                .mapToInt(card -> card.getArtistNumber(tribe))
                .sum();
    }

    //Used at end of game together with getInventorIconsNumber() to calculate: inventors × distinct icons = PP.
    public int getInventorsNumber(){
        return tribe.stream()
                .mapToInt(card -> card.getInventorsNumber(tribe))
                .sum();
    }

    //Returns the total count of invention icons matching the given set,across all cards in the tribe.
    public int getInventorIconsNumber(Set<InventorIcon> icons){
        return tribe.stream()
                .mapToInt(card -> card.getInventorIconsNumber(icons))
                .sum();
    }

    public int getFood() {return food;}

    public int getPrestigePoints() {return prestigePoints;}

    public void addCard(DrawableCard card){
        tribe.add(card);
        card.onCardAddedToTribe(tribe);
    }

    public void addFood(int amount){this.food += amount;}

    public void payFood(int amount){this.food -= amount;}

    public void addPrestige(int amount){this.prestigePoints += amount;}

    public void payPrestige(int amount){this.prestigePoints -= amount;}

    public List<DrawableCard> getTribe() { return tribe; }

// Handles the consequence of the totem landing on a turn order space. Called by Game after Board.returnTotem() identifies which space was taken.
    public void handleTotemReturn(SpaceBonus bonus){
        switch(bonus) {
            case GAIN_FOOD -> addFood(/* TODO look the amount*/);
            case PAY_FOOD -> {
                if (food >= 1) {
                    //enough food => pay normally
                    payFood(/*amount*/);
                } else {
                    //not enough food => pay PP
                    payPrestige(/*amount*/);
                }
            }
            case NONE -> {/*nothing happens*/}
        }
}

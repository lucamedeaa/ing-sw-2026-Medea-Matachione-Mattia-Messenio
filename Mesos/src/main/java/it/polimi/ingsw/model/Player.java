package it.polimi.ingsw.model;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.enums.InventorIcon;
import it.polimi.ingsw.model.enums.TotemColor;
import it.polimi.ingsw.model.enums.SpaceBonus;
import java.util.function.ToIntFunction;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class Player {
    private String name;
    private int food;
    private List<DrawableCard> tribe;
    private int prestigePoints;
    private TotemColor totemColor;

    public Player(String name, List<DrawableCard> tribe, TotemColor totemColor) {
        this.name = name;
        this.food = 0;
        this.prestigePoints = 0;
        this.tribe = new ArrayList<>(tribe);
        this.totemColor = totemColor;
    }

    public int getFoodDiscount(){
        return 0;
    }

    //ToIntFunction<DrawableCard> is a Java functional interface that represents a function that takes a DrawableCard and returns an int
    private int sumFromTribe(ToIntFunction<DrawableCard> extract){
        return tribe.stream()
                .mapToInt(card -> extract.applyAsInt(card))
                .sum();
    }

    public void addFood(int amount) {
        this.food += amount;
        if (this.food < 0){
            addPrestige(2*this.food);
            this.food = 0;
        }
    }
    public void addPrestige(int amount) {
        this.prestigePoints += amount;
    }

    public int calculateTotalScore(){
        return prestigePoints + sumFromTribe(card -> card.getFinalPoints(this));
    }

    public int getInventorIconsNumber(Set<InventorIcon> icons) {
        return sumFromTribe(card -> card.getInventorIconsNumber(icons));
    }


    public int getPrestigePoints() {
        return prestigePoints;
    }

    public int getFood() {
        return food;
    }

    public void addCard(DrawableCard newCard) {
        tribe.add(newCard);
        for(DrawableCard card : this.tribe) {
            card.onCardAddedToTribe(this, newCard);
        }
    }


    public List<DrawableCard> getTribe() {
        return tribe;
    }



    public int getHunterNumber(){return sumFromTribe(card -> card.getHunterNumber());}
    public int getCollectorNumber() { return sumFromTribe(card -> card.getCollectorNumber()); }
    public int getShamanNumber()    { return sumFromTribe(card -> card.getStarsNumber()); }
    public int getStarsNumber()     { return sumFromTribe(card -> card.getStarsNumber()); }
    public int getArtistNumber()    { return sumFromTribe(card -> card.getArtistNumber()); }
    public int getInventorsNumber() { return sumFromTribe(card -> card.getInventorsNumber()); }
    public int getBuilderDiscount() { return sumFromTribe(card -> card.getBuilderNumber()); }


}

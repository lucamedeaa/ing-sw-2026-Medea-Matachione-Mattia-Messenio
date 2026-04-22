package it.polimi.ingsw.model;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.InventorIcon;
import it.polimi.ingsw.model.enums.TotemColor;
import it.polimi.ingsw.model.enums.CharacterType;

import java.util.HashSet;
import java.util.function.ToIntFunction;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class Player {
    private String name;
    private int food;
    private List<Card> tribe;
    private int prestigePoints;
    private TotemColor totemColor;

    public Player(String name, TotemColor totemColor) {
        this.name = name;
        this.food = 0;
        this.prestigePoints = 0;
        this.tribe = new ArrayList<>();
        this.totemColor = totemColor;
    }

    public int getFoodDiscount(){
        return sumFromTribe(Card::getFoodDiscount);
    }

    //ToIntFunction<Card> is a Java functional interface that represents a function that takes a Card and returns an int
    private int sumFromTribe(ToIntFunction<Card> extract){
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

    public int getFoodBonus(){ return sumFromTribe(card -> card.getFoodBonus());}

    public void addPrestige(int amount) {
        this.prestigePoints += amount;
    }

    public int calculateTotalScore(){
        Set<InventorIcon> seenIcons = new HashSet<>();
        int distincIcons = sumFromTribe(card -> card.getInventorIconsNumber(seenIcons));

        return prestigePoints +
                sumFromTribe(card -> card.getFinalPoints(this)) +
                ((countCharactersOfType(CharacterType.ARTIST)/2) * 10) +
                countCharactersOfType(CharacterType.INVENTOR) * distincIcons ;
    }

/*    public int getInventorIconsNumber(Set<InventorIcon> icons) {
        return sumFromTribe(card -> card.getInventorIconsNumber(icons));
    }*/


    public int getPrestigePoints() {
        return prestigePoints;
    }

    public int getFood() {
        return food;
    }

    public void addCard(Card newCard) {
        tribe.add(newCard);
        newCard.onCardAddedInstantEffects(this);
        for(Card card : this.tribe) {
            card.onCardAddedToTribe(this, newCard);
        }

    }


    public List<Card> getTribe() {
        return tribe;
    }

    public int countCharactersOfType(CharacterType typeToCount) {
        return (int) tribe.stream()
                .filter(card -> card.getCharacter() == typeToCount)
                .count(); // count() restituisce long, ma lo convertiamo subito in int
    }

    public int getStarsNumber()     { return sumFromTribe(card -> card.getStarsNumber()); }

    public int getTopRowBonus() { return sumFromTribe(card -> card.getTopRowBonus());}
}

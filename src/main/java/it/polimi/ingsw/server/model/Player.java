package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.enums.InventorIcon;
import it.polimi.ingsw.server.model.enums.TotemColor;
import it.polimi.ingsw.server.model.enums.CharacterType;

import java.util.HashSet;
import java.util.function.ToIntFunction;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

/**
 * Represents a player in the game.
 * <p>
 * A player has food, prestige points, a tribe of cards, and a totem color.
 * Provides methods to manage resources, cards, and compute scores.
 */
public class Player {
    private String name;
    private int food;
    private List<Card> tribe;
    private int prestigePoints;
    private TotemColor totemColor;

    /**
     * Constructs a player.
     * @param name the player name
     * @param totemColor the player's totem color
     */
    public Player(String name, TotemColor totemColor) {
        this.name = name;
        this.food = 0;
        this.prestigePoints = 0;
        this.tribe = new ArrayList<>();
        this.totemColor = totemColor;
    }

    /**
     * Computes the discount applied during Sustenance.
     *
     * @return Sustenance discount from collectors and card effects
     */
    public int getSustenanceDiscount() {
        int discount = countCharactersOfType(CharacterType.COLLECTOR) * 3;
        for (Card card : tribe) {
            discount += card.onSustenanceEvent(this);
        }
        return discount;
    }

    /**
     * Returns the total food discount from all cards in the tribe.
     * @return total food discount
     */
    public int getFoodDiscount(){
        return sumFromTribe(Card::getFoodDiscount);
    }

    /**
     * Utility method to sum integer values extracted from tribe cards.
     * @param extract function mapping a card to an integer value
     * @return summed value
     */
    private int sumFromTribe(ToIntFunction<Card> extract){
        return tribe.stream()
                .mapToInt(card -> extract.applyAsInt(card))
                .sum();
    }

    /**
     * Adds food to the player. If food becomes negative,
     * converts the deficit into prestige loss and resets food to zero.
     * @param amount amount of food to add (can be negative)
     */
    public void addFood(int amount) {
        this.food += amount;
        if (this.food < 0){
            addPrestige(2 * this.food);
            this.food = 0;
        }
    }

    /**
     * Returns total food bonus from tribe cards.
     * @return food bonus
     */
    public int getFoodBonus(){
        return sumFromTribe(card -> card.getFoodBonus());
    }

    /**
     * Adds prestige points.
     * @param amount amount to add (can be negative)
     */
    public void addPrestige(int amount) {
        this.prestigePoints += amount;
    }

    /**
     * Computes the player's total score.
     * @return total score including prestige, card points, and bonuses
     */
    public int calculateTotalScore(){
        Set<InventorIcon> seenIcons = new HashSet<>();
        int distinctIcons = sumFromTribe(card -> card.getInventorIconsNumber(seenIcons));

        return prestigePoints +
                sumFromTribe(card -> card.getFinalPoints(this)) +
                ((countCharactersOfType(CharacterType.ARTIST) / 2) * 10) +
                countCharactersOfType(CharacterType.INVENTOR) * distinctIcons;
    }

    /**
     * Returns current prestige points.
     * @return prestige points
     */
    public int getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * Returns current food.
     * @return food amount
     */
    public int getFood() {
        return food;
    }

    /**
     * Adds a card to the player's tribe and triggers related effects.
     * @param newCard the card to add
     */
    public void addCard(Card newCard) {
        tribe.add(newCard);
        newCard.onCardAddedInstantEffects(this);
        for (Card card : this.tribe) {
            card.onCardAddedToTribe(this, newCard);
        }
    }

    /**
     * Returns the player's tribe.
     * @return list of cards
     */
    public List<Card> getTribe() {
        return tribe;
    }

    /**
     * Counts the number of cards of a given character type.
     * @param typeToCount character type to count
     * @return number of matching cards
     */
    public int countCharactersOfType(CharacterType typeToCount) {
        return (int) tribe.stream()
                .filter(card -> card.getCharacter() == typeToCount)
                .count();
    }

    /**
     * Returns total stars from tribe cards.
     * @return stars count
     */
    public int getStarsNumber() {
        return sumFromTribe(card -> card.getStarsNumber());
    }

    /**
     * Returns total top row bonus from tribe cards.
     * @return bonus value
     */
    public int getTopRowBonus() {
        return sumFromTribe(card -> card.getTopRowBonus());
    }

    /**
     * Returns the player's nickname.
     *
     * @return player nickname
     */
    public String getNickname() {
        return name;
    }

    /**
     * Returns the player's totem color.
     *
     * @return assigned totem color
     */
    public TotemColor getTotemColor() {
        return totemColor;
    }
}
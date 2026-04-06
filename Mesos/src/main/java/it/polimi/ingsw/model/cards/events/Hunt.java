package it.polimi.ingsw.model.cards.events;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

import java.util.List;

public class Hunt extends Event {
    private final int foodGiven;
    private final int prestigeGiven;
    public Hunt(int era, int foodGiven, int prestigeGiven) {
        this.era = era;
        this.foodGiven = foodGiven;
        this.prestigeGiven = prestigeGiven;
    }
    @Override
    public void execute(List<Player> players) {
        int num;
        for(Player player : players) {
            num=player.getHunterNumber();
            player.addFood(num*foodGiven);
            player.addPrestige(num*prestigeGiven);

            for (DrawableCard card : player.getTribe()) {
                card.onHuntEvent(player);
            }
        }
    }
}
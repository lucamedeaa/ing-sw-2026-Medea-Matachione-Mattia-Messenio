package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;
import it.polimi.ingsw.model.enums.CharacterType;

import java.util.List;

public class Sustenance extends Event {
    private final int numPrestRem;

    public Sustenance(int era, int numPrestRem) {
        this.era = era;
        this.numPrestRem = numPrestRem;
    }

    @Override
    public void execute(List<Player> players) {
        for (Player player : players) {
            int discount = player.countCharactersOfType(CharacterType.COLLECTOR) * 3;

            for (DrawableCard card : player.getTribe()) {
                discount += card.onSustenanceEvent(player);
            }

            int total = 0;
            int playerFood = player.getFood();
            for (DrawableCard card : player.getTribe()) {
                if (!(card.getCharacter().equals(CharacterType.BUILDING))) {
                    total += 1;
                }
            }

            if (playerFood + discount < total) {
                player.addFood(-playerFood);
                player.addPrestige(-numPrestRem * (total - (playerFood + discount)));
            } else if (total > discount) {
                player.addFood(-(total-discount));
            }
        }

    }
    @Override
    public int getResolutionPriority() {
        return 1;
    }
}



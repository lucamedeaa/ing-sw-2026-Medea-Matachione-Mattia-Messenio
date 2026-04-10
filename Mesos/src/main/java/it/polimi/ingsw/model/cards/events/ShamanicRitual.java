package it.polimi.ingsw.model.cards.events;
import java.util.List;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class ShamanicRitual extends Event {
    private final int incrPrestigePoints;
    private final int decrPrestigePoints; //negative number

    public ShamanicRitual(int era, int incrPrestigePoints, int decrPrestigePoints) {
        this.era = era;
        this.incrPrestigePoints = incrPrestigePoints;
        this.decrPrestigePoints = decrPrestigePoints;
    }

    @Override
    public void execute(List<Player> players) {

        int[] stars = new int[players.size()];

        for (int i = 0; i < players.size(); i++) {
            stars[i] = players.get(i).getStarsNumber();
            for (DrawableCard card : players.get(i).getTribe()) {
                stars[i] += card.onShamanicRitualEvent(players.get(i), 0, 0);
            }
        }

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (int x : stars) {
            max = Math.max(max, x);
            min = Math.min(min, x);
        }

        for (int i = 0; i < players.size(); i++) {
            if (stars[i] == max) {
                players.get(i).addPrestige(incrPrestigePoints);
                for(DrawableCard card : players.get(i).getTribe()) {
                    card.onShamanicRitualEvent(players.get(i), incrPrestigePoints, 0);
                }
            }
            if (stars[i] == min) {
                players.get(i).addPrestige(decrPrestigePoints);
                for(DrawableCard card : players.get(i).getTribe()) {
                    card.onShamanicRitualEvent(players.get(i), 0, decrPrestigePoints);
                }
            }
        }
    }
}

//0s in onShamanicRitualEvent are on purpose
//in case of a complete draw (max = min), rules state 'points get first added then removed', both if's have effect
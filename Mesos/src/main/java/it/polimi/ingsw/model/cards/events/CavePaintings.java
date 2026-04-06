package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

import java.util.List;

public class CavePaintings extends Event {
    private final int minArtists;
    private final int lowerNumArtists;
    private final int upperNumArtists;
    private final int decrPrestigePoints;
    private final int incrPrestigePoints;

    public CavePaintings(int era, int minArtists, int lowerNumArtists, int upperNumArtists, int decrPrestigePoints, int incrPrestigePoints) {
        this.era=era;
        this.minArtists = minArtists;
        this.lowerNumArtists = lowerNumArtists;
        this.upperNumArtists = upperNumArtists;
        this.decrPrestigePoints = decrPrestigePoints;
        this.incrPrestigePoints = incrPrestigePoints;
    }
    @Override
    public void execute(List<Player> players) {
        for (Player player : players) {
            if(player.getArtistNumber() == upperNumArtists){
                player.payPrestige(decrPrestigePoints);

            }else if(player.getArtistNumber() >= lowerNumArtists && player.getArtistNumber() >= minArtists){
                player.addPrestige(incrPrestigePoints*player.getArtistNumber());
            }

            for (DrawableCard card : player.getTribe()) {
                card.onCavePaintingsEvent(player);
            }
        }
    }
}
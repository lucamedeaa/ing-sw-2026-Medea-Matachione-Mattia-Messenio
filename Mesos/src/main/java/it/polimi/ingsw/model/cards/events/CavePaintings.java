package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.enums.CharacterType;

import java.util.List;

public class CavePaintings extends Event {
    private final int lowerNumArtists;
    private final int upperNumArtists;
    private final int decrPrestigePoints;
    private final int incrPrestigePoints;

    public CavePaintings(int era, int lowerNumArtists, int upperNumArtists, int decrPrestigePoints, int incrPrestigePoints) {
        this.era=era;
        this.lowerNumArtists = lowerNumArtists;
        this.upperNumArtists = upperNumArtists;
        this.decrPrestigePoints = decrPrestigePoints;
        this.incrPrestigePoints = incrPrestigePoints;
    }
    
    @Override
    public void execute(List<Player> players) {

        for (Player player : players) {
            int artistNumber = player.countCharactersOfType(CharacterType.ARTIST);

            for(DrawableCard card : player.getTribe()){
                card.onCavePaintingsEvent(player);
            }
            if(artistNumber <= upperNumArtists){
                player.addPrestige(decrPrestigePoints);

            }else if(artistNumber >= lowerNumArtists){
                player.addPrestige(incrPrestigePoints*artistNumber);
            }

            for (DrawableCard card : player.getTribe()) {
                card.onCavePaintingsEvent(player);
            }
        }
    }
}
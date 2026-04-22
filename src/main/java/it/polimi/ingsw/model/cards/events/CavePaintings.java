package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.CharacterType;

import java.util.List;

public class CavePaintings extends Event {
    private final int upperNumArtists;
    private final int decrPrestigePoints;
    private final int incrPrestigePoints;

    public CavePaintings(int idcard, int era, int upperNumArtists, int decrPrestigePoints, int incrPrestigePoints) {
        super(idcard,era);
        this.upperNumArtists = upperNumArtists;
        this.decrPrestigePoints = decrPrestigePoints;
        this.incrPrestigePoints = incrPrestigePoints;
    }

    @Override
    public void execute(List<Player> players) {

        for (Player player : players) {
            int artistNumber = player.countCharactersOfType(CharacterType.ARTIST);

            if(artistNumber < upperNumArtists){
                player.addPrestige(decrPrestigePoints);

            }else{
                player.addPrestige(incrPrestigePoints*artistNumber);
            }

            for (Card card : player.getTribe()) {
                card.onCavePaintingsEvent(player);
            }
        }
    }
}
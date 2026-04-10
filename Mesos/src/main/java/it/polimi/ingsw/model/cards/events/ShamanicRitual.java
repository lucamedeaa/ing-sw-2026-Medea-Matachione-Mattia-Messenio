package it.polimi.ingsw.model.cards.events;
import java.util.List;
import it.polimi.ingsw.model.Player;

public class ShamanicRitual extends Event {
    private int incrPrestigePoints;
    private int decrPrestigePoints;
    private Boolean isFinal;
    public ShamanicRitual(int era, int incrPrestigePoints, int decrPrestigePoints) {

    }
    @Override
    public void execute(List<Player> players){}
}

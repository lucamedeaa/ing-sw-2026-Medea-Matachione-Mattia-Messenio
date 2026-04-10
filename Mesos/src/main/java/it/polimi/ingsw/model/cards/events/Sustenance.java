package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.events.Event;

import java.util.List;

public class Sustenance extends Event {
    private int numPrestRem;
    private Boolean isFinal;
    @Override
    public void execute(List<Player> players) {
        super.execute(players);
    }
    @Override
    public int getResolutionPriority() {
        return 1;
    }
    public Sustenance(int era, int numPrestRem) {

    }
}

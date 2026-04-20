package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;

import java.util.List;

public class Sustenance extends Event {
    private int numPrestRem;
    private Boolean isFinal;
    @Override
    public void execute(List<Player> players) {
        super.execute(players);
    }
    public Sustenance(int numPrestRem, Boolean isFinal) {}
}

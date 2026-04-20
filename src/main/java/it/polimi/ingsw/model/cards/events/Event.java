package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import java.util.List;

public abstract class Event {
    public void execute(List<Player> players) {}
}


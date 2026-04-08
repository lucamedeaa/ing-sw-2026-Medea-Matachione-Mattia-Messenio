package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import java.util.List;
import it.polimi.ingsw.model.cards.Card;

public abstract class Event extends Card{
    public void execute(List<Player> players) {}
}


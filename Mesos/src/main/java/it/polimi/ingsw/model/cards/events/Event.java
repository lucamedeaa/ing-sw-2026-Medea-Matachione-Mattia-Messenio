package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;

import java.util.List;

public abstract class Event extends Card {
    public void execute(List<Player> players) {};
}


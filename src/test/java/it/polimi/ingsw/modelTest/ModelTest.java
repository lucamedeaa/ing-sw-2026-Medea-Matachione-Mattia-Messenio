package it.polimi.ingsw.modelTest;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.enums.TotemColor;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelTest {
    protected Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    protected void give(Player p, Card card) {
        p.addCard(card);
    }

    protected List<Player> newPlayers(int n) {
        List<Player> players = new ArrayList<>();
        TotemColor[] colors = TotemColor.values();
        for (int i = 0; i < n; i++) {
            players.add(new Player("Player" + (i + 1), colors[i % colors.length]));
        }
        return players;
    }
}

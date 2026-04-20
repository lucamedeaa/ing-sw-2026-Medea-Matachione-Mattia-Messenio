package it.polimi.ingsw.model.board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enums.TotemColor;

import java.util.List;

public class TurnOrderTile {
    private List<Player> totemSpaces;
    private List<Integer> foodBonus;

    public TurnOrderTile(List<Player> totemSpaces, List<Integer> foodBonus){}
    public TotemColor returnTotem(Player player){ return null;}
    public void manageBonus(){}

}

package it.polimi.ingsw.model.board;
import it.polimi.ingsw.model.Player;
import java.util.Optional;

public class OfferTile {
    private char letterId;
    private Optional<Player> occupyingPlayer;
    private int upperRowPicks;
    private int lowerRowPicks;
    private Boolean foodBonus;

    public OfferTile(){}
    public Boolean isFree(){return false;}
}

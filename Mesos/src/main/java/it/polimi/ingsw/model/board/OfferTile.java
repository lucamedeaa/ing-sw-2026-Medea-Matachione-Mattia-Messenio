package it.polimi.ingsw.model.board;
import it.polimi.ingsw.model.Player;
import java.util.Optional;

public class OfferTile {
    private final char letterId;
    private Optional<Player> occupyingPlayer;
    private final int upperRowPicks;
    private final int lowerRowPicks;
    private Boolean foodBonus;

    public OfferTile(char letterId, int upperRowPicks, int lowerRowPicks, boolean foodBonus) {
        this.letterId = letterId;
        this.upperRowPicks = upperRowPicks;
        this.lowerRowPicks = lowerRowPicks;
        this.foodBonus = foodBonus;

        //start -> no player
        this.occupyingPlayer = Optional.empty();
    }
    public Boolean isFree(){
        return occupyingPlayer.isEmpty(); //true if no player
    }

    public Optional<Player> getOccupyingPlayer() {return occupyingPlayer;}
    public int getUpperRowPicks() {return upperRowPicks;}
    public int getLowerRowPicks() {return lowerRowPicks;}
    public char getLetterId() {return letterId;}
    public boolean getFoodBonus() {return foodBonus;}

}



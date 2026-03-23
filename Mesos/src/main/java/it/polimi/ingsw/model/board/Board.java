package it.polimi.ingsw.model.board;
import it.polimi.ingsw.model.Deck;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import java.util.List;

public class Board {
    private List<Card> upperRow;
    private List<Card> lowerRow;
    private List<OfferTile> offerTrack;
    private TurnOrderTile turnOrderTile;
    private Deck tribeDeck;
    private Deck buildingDeck;
    private int currentEra;
    private List<Integer> counterCardEra;

    public Board(){}
    public void refreshBoard(int playerCount){}
    public void resolveEvents(List<Player> players){}
    public Boolean allTotemsPlaced(){return false;}
    public void refillTopRow(){}
    public void moveToBottom(){}
    public void removeBottomRow(){}
    public void shiftBuildingsToLow(){}
    public void clearBuildingsLow(){}



}

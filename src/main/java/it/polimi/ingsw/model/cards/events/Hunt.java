package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.enums.CharacterType;
import java.util.List;

public class Hunt extends Event {
    private final int foodGiven;
    private final int prestigeGiven;
    public Hunt(int idcard, int era, int foodGiven, int prestigeGiven) {
        super(idcard,era);
        this.foodGiven = foodGiven; //teoricamente inutile visto che è sempre 1
        this.prestigeGiven = prestigeGiven;
    }

    @Override
    public void execute(List<Player> players) {
        int num;
        for(Player player : players) {
            num=player.countCharactersOfType(CharacterType.HUNTER);
            player.addFood(num*foodGiven);
            player.addPrestige(num*prestigeGiven);
            for (Card card : player.getTribe()) {
                card.onHuntEvent(player);
            }
        }
    }
}
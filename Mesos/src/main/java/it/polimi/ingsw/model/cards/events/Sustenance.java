package it.polimi.ingsw.model.cards.events;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.buildings.Building;

import java.util.List;

public class Sustenance extends Event {
    private final int numPrestRem;
    private final Boolean isFinal;

    public Sustenance(int era, int numPrestRem, Boolean isFinal) {
        this.era = era;
        this.numPrestRem = numPrestRem;
        this.isFinal = isFinal;
    }
    @Override
    public void execute(List<Player> players) {
        if(isFinal) {
            return;
            //TODO:execute() when the card is final
        }
        for (Player player : players) {
            int discount = player.getCollectorNumber()*3;

            for (DrawableCard card : player.getTribe()) {
                discount += card.onSustenanceEvent(player);
            }
            int total=0;
            int playerFood = player.getFood();
            for(DrawableCard card : player.getTribe()){
                if(!(card instanceof Building)){
                    total+=1;
                }
            }
            if(playerFood+discount < total){
                player.payFood(playerFood);
                player.payPrestige(numPrestRem*(total-(playerFood+discount)));
            }else if(total > discount){
                player.payFood(total-discount);
            }

        }
    }

}
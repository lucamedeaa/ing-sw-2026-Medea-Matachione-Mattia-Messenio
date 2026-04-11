package it.polimi.ingsw.model.cards.events;
import java.util.List;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;

public class ShamanicRitual extends Event {
    private final int incrPrestigePoints;
    private final int decrPrestigePoints; //negative number

    public ShamanicRitual(int era, int incrPrestigePoints, int decrPrestigePoints) {
        this.era = era;
        this.incrPrestigePoints = incrPrestigePoints;
        this.decrPrestigePoints = decrPrestigePoints;
    }

    @Override
    public void execute(List<Player> players){
            int max=0;
            int cnt;
            for(Player player : players){
                cnt=0;
                for(DrawableCard card : player.getTribe()){
                    cnt+=card.getStarsNumber();
                }
                if(cnt>max){
                    max=cnt;
                }
            }
            if(max!=0){
                for(Player player : players){
                    cnt=0;
                    for(DrawableCard card : player.getTribe()){
                        cnt+=card.getStarsNumber();
                    }
                    if(cnt!=max){
                        player.addPrestige(-decrPrestigePoints);
                        for(DrawableCard card : player.getTribe()) {
                            card.onShamanicRitualEvent(player, 0, decrPrestigePoints);
                        }
                    }else{
                        player.addPrestige(incrPrestigePoints);
                        for(DrawableCard card : player.getTribe()) {
                            card.onShamanicRitualEvent(player, incrPrestigePoints, 0);
                        }
                    }
                }
            }
    }
}


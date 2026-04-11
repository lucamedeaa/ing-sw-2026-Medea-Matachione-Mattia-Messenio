package it.polimi.ingsw.model.cards.events;
import java.util.List;
import it.polimi.ingsw.model.Player;

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
                        player.payPrestige(decrPrestigePoints);
                    }else{
                        player.addPrestige(incrPrestigePoints);
                    }
                }
            }
/*cbg ha fatto
    public void execute(List<Player> players) {

        int[] stars = new int[players.size()];

        for (int i = 0; i < players.size(); i++) {
            stars[i] = players.get(i).getStarsNumber();
            for (DrawableCard card : players.get(i).getTribe()) {
                stars[i] += card.onShamanicRitualEvent(players.get(i), 0, 0);
            }
        }

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (int x : stars) {
            max = Math.max(max, x);
            min = Math.min(min, x);
        }

        for (int i = 0; i < players.size(); i++) {
            if (stars[i] == max) {
                players.get(i).addPrestige(incrPrestigePoints);
                for(DrawableCard card : players.get(i).getTribe()) {
                    card.onShamanicRitualEvent(players.get(i), incrPrestigePoints, 0);
                }
            }
            if (stars[i] == min) {
                players.get(i).addPrestige(decrPrestigePoints);
                for(DrawableCard card : players.get(i).getTribe()) {
                    card.onShamanicRitualEvent(players.get(i), 0, decrPrestigePoints);
                }
            }
        }
    }
}
*/


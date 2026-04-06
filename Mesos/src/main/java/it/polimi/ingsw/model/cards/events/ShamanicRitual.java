package it.polimi.ingsw.model.cards.events;
import java.util.List;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.characters.Shaman;

public class ShamanicRitual extends Event {
    private final int incrPrestigePoints;
    private final int decrPrestigePoints;
    private final Boolean isFinal;

    public ShamanicRitual(int era, int incrPrestigePoints, int decrPrestigePoints, Boolean isFinal) {
        this.era=era;
        this.incrPrestigePoints = incrPrestigePoints;
        this.decrPrestigePoints = decrPrestigePoints;
        this.isFinal = isFinal;
    }
    @Override
    public void execute(List<Player> players){
        if(isFinal){
            //TODO: execute() when the card is Final
            return;
        }
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

        }
    }
//Please check the compliance with this edge case ==> "Nota: nel raro caso di un pareggio tra tutti i giocatori, tutti prima
//guadagnano PP e poi perdono PP. Questo è importante se alcune
//carte Edificio sono in gioco."
package it.polimi.ingsw.server.model.card.building;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.card.character.Inventor;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.InventorIcon;

import java.util.EnumMap;

/** Building that grants food when pairs of identical INVENTOR icons are formed. */
public class InventorPair extends Building {

    private boolean init;
    private final EnumMap<InventorIcon, Integer> iconCount;

    /** Constructs the InventorPair building and initializes icon tracking. @param idcard the card identifier @param foodCost food cost to acquire @param prestigePoints base prestige points @param era the card era */
    public InventorPair(int idcard, int foodCost, int prestigePoints, int era) {
        super(idcard, foodCost, prestigePoints, era);
        init = false;
        iconCount = new EnumMap<>(InventorIcon.class);
        for (InventorIcon icon : InventorIcon.values()) {
            iconCount.put(icon, 0);
        }
    }

    /** Tracks INVENTOR icons and grants food when a pair of identical icons is completed. @param owner the owning player @param newcard the newly added card */
    @Override
    public void onCardAddedToTribe(Player owner, Card newcard){
        InventorIcon icon;
        if (!init){
            for (Card card : owner.getTribe()){
                if (card.getCharacter().equals(CharacterType.INVENTOR)){
                    icon = ((Inventor) card).getInventorIcon();
                    iconCount.put(icon, (iconCount.get(icon) + 1) % 2);
                }
            }
            init = true;
            return;
        }
        if (newcard.getCharacter().equals(CharacterType.INVENTOR)){
            icon = ((Inventor) newcard).getInventorIcon();
            iconCount.put(icon, (iconCount.get(icon) + 1) % 2);

            if (iconCount.get(icon) == 0){
                owner.addFood(3);
            }
        }
    }
}
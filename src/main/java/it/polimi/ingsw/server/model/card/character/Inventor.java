package it.polimi.ingsw.server.model.card.character;

import it.polimi.ingsw.common.config.CardRegistry;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.InventorIcon;

import java.util.Set;

/** Represents an Inventor character card that contributes a unique inventor icon for scoring. */
public class Inventor extends Character {
    private final InventorIcon inventorIcon;

    /** Constructs an Inventor card.
     * @param idcard the card identifier */
    public Inventor(int idcard) {
        super(idcard);
        this.foodCost = 0;
        this.inventorIcon = InventorIcon.valueOf(CardRegistry.getCard(idcard).inventorIcon());
    }

    /** Returns the character type. @return CharacterType.INVENTOR */
    @Override
    public CharacterType getCharacter() {
        return CharacterType.INVENTOR;
    }

    /** Adds the icon to the set if not already present and returns its contribution. @param inventorIcons set of collected icons @return 1 if new, 0 otherwise */
    @Override
    public int getInventorIconsNumber(Set<InventorIcon> inventorIcons){
        if (inventorIcons.contains(inventorIcon)){
            return 0;
        }
        inventorIcons.add(inventorIcon);
        return 1;
    }

    /** Returns the inventor icon associated with this card. @return inventor icon */
    public InventorIcon getInventorIcon() {
        return inventorIcon;
    }
}
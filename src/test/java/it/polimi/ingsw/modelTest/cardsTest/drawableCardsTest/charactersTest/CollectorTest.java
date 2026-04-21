package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.characters.Artist;
import it.polimi.ingsw.model.cards.drawableCards.characters.Collector;
import it.polimi.ingsw.model.cards.events.Sustenance;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectorTest extends ModelTest {

    /**
     * each Collector gives 3 food discount on Sustenance.
     * Does NOT give food directly when added.
     */
    @Test
    @DisplayName("Adding a Collector does not give direct food")
    void collectorGivesNoFoodWhenAdded() {
        Player p = newPlayer("Eve");
        give(p, new Collector(1, 3));
        assertEquals(0, p.getFood());
    }

    /**
     * With 1 Collector (discount=3) and 3 characters: total food due=3, discount=3 → no loss.
     * Without Collector: with 0 food and 3 characters loses 3×PP.
     */
    @Test
    @DisplayName("1 Collector with discount 3 exactly covers 3 characters at Sustenance")
    void collectorDiscountCoversThreeCharacters() {
        Player p = newPlayer("Eve");
        give(p, new Collector(1, 3));
        give(p, new Artist(1));
        give(p, new Artist(1));
        // food=0, characters=3, collector discount=3 → discount=3, total=3 → ok, no loss
        Sustenance s = new Sustenance(1, 2);
        s.execute(List.of(p));
        assertEquals(0, p.getPrestigePoints(), "With discount equal to cost no PP should be lost");
        assertEquals(0, p.getFood());
    }
}

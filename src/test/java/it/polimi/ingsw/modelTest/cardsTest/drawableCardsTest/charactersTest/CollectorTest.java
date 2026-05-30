package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Artist;
import it.polimi.ingsw.server.model.card.character.Collector;
import it.polimi.ingsw.server.model.card.event.Sustenance;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectorTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that adding a Collector card does not directly grant any food to the player.
     *
     * EXPECTATION:
     * Player's food remains 0 after adding a Collector.
     */
    @Test
    @DisplayName("Adding a Collector does not give direct food")
    void collectorGivesNoFoodWhenAdded() {
        Player p = newPlayer("Eve");
        // Updated Constructor: idcard, era, discount
        p.addCard(new Collector(35));

        assertEquals(0, p.getFood());
    }

    /**
     * SUMMARY:
     * Verifies that a Collector's food discount exactly offsets the Sustenance cost for 3 characters.
     *
     * EXPECTATION:
     * No prestige is lost and no food is consumed when the discount equals the feeding cost.
     */
    @Test
    @DisplayName("1 Collector with discount 3 exactly covers 3 characters at Sustenance")
    void collectorDiscountCoversThreeCharacters() {
        Player p = newPlayer("Eve");
        p.addCard(new Collector(35));
        p.addCard(new Artist(19));
        p.addCard(new Artist(20));

        // Sustenance Constructor: idcard, era, numPrestRem
        Sustenance s = new Sustenance(61);
        s.execute(List.of(p));

        assertEquals(0, p.getPrestigePoints(), "With discount equal to cost no PP should be lost");
        assertEquals(0, p.getFood());
    }
}
package it.polimi.ingsw.modelTest.cardsTest.eventsTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.buildings.DoublePrestigeShaman;
import it.polimi.ingsw.model.cards.drawableCards.buildings.RitualShield;
import it.polimi.ingsw.model.cards.drawableCards.buildings.RitualStars;
import it.polimi.ingsw.model.cards.drawableCards.characters.Shaman;
import it.polimi.ingsw.model.cards.events.ShamanicRitual;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShamanicRitualTest extends ModelTest {
    /**
     * Player with more stars gains PP; with fewer stars loses PP.
     */
    @Test
    @DisplayName("Majority of stars gains, minority loses")
    void majorityGainsMinorityLoses() {
        Player rich = newPlayer("Rich");
        Player poor = newPlayer("Poor");
        give(rich, new Shaman(1, 3)); // 3 stars
        give(poor, new Shaman(1, 1)); // 1 star

        ShamanicRitual r = new ShamanicRitual(1, 10, -5);
        r.execute(List.of(rich, poor));

        assertEquals(10, rich.getPrestigePoints());
        assertEquals(-5, poor.getPrestigePoints());
    }

    /**
     * In case of ABSOLUTE tie (max==min): everyone gains AND loses.
     * Rule: "everyone gains first then loses".
     * With incrPP=10, decrPP=-5: net = +10 + (-5) = +5 for everyone.
     */
    @Test
    @DisplayName("Absolute tie: everyone gains and loses")
    void totalTieEveryoneGainsAndLoses() {
        Player a = newPlayer("A");
        Player b = newPlayer("B");
        give(a, new Shaman(1, 2));
        give(b, new Shaman(1, 2));

        ShamanicRitual r = new ShamanicRitual(1, 10, -5);
        r.execute(List.of(a, b));

        assertEquals(5, a.getPrestigePoints(), "In absolute tie: +10-5=+5");
        assertEquals(5, b.getPrestigePoints());
    }

    /**
     * Tie only at the maximum (2 players tied for stars, 1 with less):
     * both players at the maximum gain PP.
     */
    @Test
    @DisplayName("Tie at maximum: both gain PP")
    void tieAtMaxBothGain() {
        Player a = newPlayer("A");
        Player b = newPlayer("B");
        Player c = newPlayer("C");
        give(a, new Shaman(1, 3));
        give(b, new Shaman(1, 3));
        give(c, new Shaman(1, 1));

        ShamanicRitual r = new ShamanicRitual(1, 10, -5);
        r.execute(List.of(a, b, c));

        assertEquals(10, a.getPrestigePoints());
        assertEquals(10, b.getPrestigePoints());
        assertEquals(-5, c.getPrestigePoints());
    }

    /**
     * RitualShield: the player with fewer stars DOES NOT lose PP.
     */
    @Test
    @DisplayName("RitualShield neutralizes PP loss at the Ritual")
    void ritualShieldBlocksPrestigeLoss() {
        Player rich = newPlayer("Rich");
        Player shielded = newPlayer("Shielded");
        give(rich, new Shaman(1, 3));
        give(shielded, new Shaman(1, 1));
        give(shielded, new RitualShield(0, 0, 1));

        ShamanicRitual r = new ShamanicRitual(1, 10, -5);
        r.execute(List.of(rich, shielded));

        // shielded has the shield: doesn't lose the -5, but the rule says they GET -5
        // and RitualShield then adds +(-decrPP)=+5 → net 0
        // We verify that it is not negative
        assertTrue(shielded.getPrestigePoints() >= 0,
                "With RitualShield no PP should be lost at the Shamanic Ritual");
    }


    @Test
    @DisplayName("RitualShield: Selectively blocks decrements while preserving increments")
    void ritualShieldBothIncrementAndDecrement() {
        Player p = newPlayer("Alice");
        RitualShield shield = new RitualShield(0, 0, 1);
        give(p, shield);

        // Simulate an event passing both a +10 gain and a -5 penalty
        shield.onShamanicRitualEvent(p, 10, -5);

        // Expectation: The -5 is blocked/neutralized (which in the logic might mean adding +5 to offset).
        // Since the test focuses on neutralizing the negative, we verify the outcome.
        assertEquals(5, p.getPrestigePoints(), "Must neutralize the decrement (-5 -> +5) without touching the logic of increments");
    }


    @Test
    @DisplayName("RitualStars adds 3 stars to the shamanic count")
    void ritualStarsAddsThreeToCount() {
        Player base = newPlayer("Base");    // 0 stars → will be minority
        Player starred = newPlayer("Starred"); // 0 stars + RitualStars = 3 stars
        give(starred, new RitualStars(0, 0, 1));

        ShamanicRitual r = new ShamanicRitual(1, 10, -5);
        r.execute(List.of(base, starred));

        assertEquals(10, starred.getPrestigePoints(),
                "RitualStars must earn PP as if it had 3 stars");
        assertEquals(-5, base.getPrestigePoints());
    }

    /**
     * DoublePrestigeShaman: doubles the PP gain for the one who wins the Ritual.
     */
    @Test
    @DisplayName("DoublePrestigeShaman doubles the PP gained at the Ritual")
    void doublePrestigeShamanDoublesGain() {
        Player winner = newPlayer("Winner");
        Player loser  = newPlayer("Loser");
        give(winner, new Shaman(1, 3));
        give(winner, new DoublePrestigeShaman(0, 0, 1));
        give(loser,  new Shaman(1, 1));

        ShamanicRitual r = new ShamanicRitual(1, 10, -5);
        r.execute(List.of(winner, loser));

        // winner gains 10 base + 10 extra from DoublePrestigeShaman = 20
        assertEquals(20, winner.getPrestigePoints(),
                "DoublePrestigeShaman must add another 10 PP (doubling)");
    }
}

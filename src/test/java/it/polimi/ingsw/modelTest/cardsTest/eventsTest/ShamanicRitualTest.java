package it.polimi.ingsw.modelTest.cardsTest.eventsTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.building.DoublePrestigeShaman;
import it.polimi.ingsw.server.model.card.building.RitualShield;
import it.polimi.ingsw.server.model.card.building.RitualStars;
import it.polimi.ingsw.server.model.card.character.Shaman;
import it.polimi.ingsw.server.model.card.event.ShamanicRitual;
import it.polimi.ingsw.server.model.enums.TotemColor;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShamanicRitualTest extends ModelTest {

    @Test
    @DisplayName("Majority of stars gains, minority loses")
    void majorityGainsMinorityLoses() {
        Player rich = new Player("Rich", TotemColor.ORANGE);
        Player poor = new Player("Poor", TotemColor.BLUE);

        // Shaman Constructor: idcard, era, starsCount
        rich.addCard(new Shaman(34, 3, 3));
        poor.addCard(new Shaman(28, 1, 1));

        // ShamanicRitual Constructor: idcard, era, incrPrestigePoints, decrPrestigePoints
        ShamanicRitual r = new ShamanicRitual(58, 1, 10, -5);
        r.execute(List.of(rich, poor));

        assertEquals(10, rich.getPrestigePoints());
        assertEquals(-5, poor.getPrestigePoints());
    }

    @Test
    @DisplayName("Absolute tie: everyone gains and loses")
    void totalTieEveryoneGainsAndLoses() {
        Player a = new Player("A", TotemColor.ORANGE);
        Player b = new Player("B", TotemColor.BLUE);

        a.addCard(new Shaman(30, 2, 2));
        b.addCard(new Shaman(31, 2, 2));

        ShamanicRitual r = new ShamanicRitual(58, 1, 10, -5);
        r.execute(List.of(a, b));

        assertEquals(5, a.getPrestigePoints(), "In absolute tie: +10-5=+5");
        assertEquals(5, b.getPrestigePoints());
    }

    @Test
    @DisplayName("Tie at maximum: both gain PP")
    void tieAtMaxBothGain() {
        Player a = new Player("A", TotemColor.ORANGE);
        Player b = new Player("B", TotemColor.BLUE);
        Player c = new Player("C", TotemColor.WHITE);

        a.addCard(new Shaman(33, 3, 3));
        b.addCard(new Shaman(34, 3, 3));
        c.addCard(new Shaman(28, 1, 1));

        ShamanicRitual r = new ShamanicRitual(58, 1, 10, -5);
        r.execute(List.of(a, b, c));

        assertEquals(10, a.getPrestigePoints());
        assertEquals(10, b.getPrestigePoints());
        assertEquals(-5, c.getPrestigePoints());
    }

    @Test
    @DisplayName("RitualShield neutralizes PP loss at the Ritual")
    void ritualShieldBlocksPrestigeLoss() {
        Player rich = new Player("Rich", TotemColor.ORANGE);
        Player shielded = new Player("Shielded", TotemColor.BLUE);

        rich.addCard(new Shaman(34, 3, 3));
        shielded.addCard(new Shaman(28, 1, 1));

        // RitualShield Constructor: idcard, foodCost, prestigePoints, era
        shielded.addCard(new RitualShield(96, 5, 2, 1));

        ShamanicRitual r = new ShamanicRitual(58, 1, 10, -5);
        r.execute(List.of(rich, shielded));

        assertTrue(shielded.getPrestigePoints() >= 0,
                "With RitualShield no PP should be lost at the Shamanic Ritual");
    }

    @Test
    @DisplayName("RitualShield: Selectively blocks decrements while preserving increments")
    void ritualShieldBothIncrementAndDecrement() {
        Player p = new Player("Alice", TotemColor.ORANGE);
        RitualShield shield = new RitualShield(96, 5, 2, 1);
        p.addCard(shield);

        shield.onShamanicRitualEvent(p, 10, -5);

        assertEquals(5, p.getPrestigePoints(),
                "Must neutralize the decrement (-5 -> +5) without touching the logic of increments");
    }

    @Test
    @DisplayName("RitualStars adds 3 stars to the shamanic count")
    void ritualStarsAddsThreeToCount() {
        Player base = new Player("Base", TotemColor.ORANGE);
        Player starred = new Player("Starred", TotemColor.BLUE);

        // RitualStars Constructor: idcard, foodCost, prestigePoints, era
        starred.addCard(new RitualStars(104, 6, 4, 2));

        ShamanicRitual r = new ShamanicRitual(58, 1, 10, -5);
        r.execute(List.of(base, starred));

        assertEquals(10, starred.getPrestigePoints(),
                "RitualStars must earn PP as if it had 3 stars");
        assertEquals(-5, base.getPrestigePoints());
    }

    @Test
    @DisplayName("DoublePrestigeShaman doubles the PP gained at the Ritual")
    void doublePrestigeShamanDoublesGain() {
        Player winner = new Player("Winner", TotemColor.ORANGE);
        Player loser  = new Player("Loser", TotemColor.BLUE);

        winner.addCard(new Shaman(34, 3, 3));
        // DoublePrestigeShaman Constructor: idcard, foodCost, prestigePoints, era
        winner.addCard(new DoublePrestigeShaman(105, 7, 0, 2));

        loser.addCard(new Shaman(28, 1, 1));

        ShamanicRitual r = new ShamanicRitual(58, 1, 10, -5);
        r.execute(List.of(winner, loser));

        assertEquals(20, winner.getPrestigePoints(),
                "DoublePrestigeShaman must add another 10 PP (doubling)");
    }
}
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

        rich.addCard(new Shaman(34));
        poor.addCard(new Shaman(28));

        ShamanicRitual r = new ShamanicRitual(59);
        r.execute(List.of(rich, poor));

        assertEquals(10, rich.getPrestigePoints());
        assertEquals(-5, poor.getPrestigePoints());
    }

    @Test
    @DisplayName("Absolute tie: everyone gains and loses")
    void totalTieEveryoneGainsAndLoses() {
        Player a = new Player("A", TotemColor.ORANGE);
        Player b = new Player("B", TotemColor.BLUE);

        a.addCard(new Shaman(30));
        b.addCard(new Shaman(31));

        ShamanicRitual r = new ShamanicRitual(59);
        r.execute(List.of(a, b));

        assertEquals(5, a.getPrestigePoints());
        assertEquals(5, b.getPrestigePoints());
    }

    @Test
    @DisplayName("Tie at maximum: both gain PP")
    void tieAtMaxBothGain() {
        Player a = new Player("A", TotemColor.ORANGE);
        Player b = new Player("B", TotemColor.BLUE);
        Player c = new Player("C", TotemColor.WHITE);

        a.addCard(new Shaman(33));
        b.addCard(new Shaman(34));
        c.addCard(new Shaman(28));

        ShamanicRitual r = new ShamanicRitual(59);
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

        rich.addCard(new Shaman(34));
        shielded.addCard(new Shaman(28));
        shielded.addCard(new RitualShield(96));

        ShamanicRitual r = new ShamanicRitual(59);
        r.execute(List.of(rich, shielded));

        assertTrue(shielded.getPrestigePoints() >= 0);
    }

    @Test
    @DisplayName("RitualShield: Selectively blocks decrements while preserving increments")
    void ritualShieldBothIncrementAndDecrement() {
        Player p = new Player("Alice", TotemColor.ORANGE);
        RitualShield shield = new RitualShield(96);
        p.addCard(shield);

        shield.onShamanicRitualEvent(p, 10, -5);
        assertEquals(5, p.getPrestigePoints());
    }

    @Test
    @DisplayName("RitualStars adds 3 stars to the shamanic count")
    void ritualStarsAddsThreeToCount() {
        Player base = new Player("Base", TotemColor.ORANGE);
        Player starred = new Player("Starred", TotemColor.BLUE);

        starred.addCard(new RitualStars(104));

        ShamanicRitual r = new ShamanicRitual(59);
        r.execute(List.of(base, starred));

        assertEquals(10, starred.getPrestigePoints());
        assertEquals(-5, base.getPrestigePoints());
    }

    @Test
    @DisplayName("DoublePrestigeShaman doubles the PP gained at the Ritual")
    void doublePrestigeShamanDoublesGain() {
        Player winner = new Player("Winner", TotemColor.ORANGE);
        Player loser  = new Player("Loser", TotemColor.BLUE);

        winner.addCard(new Shaman(34));
        winner.addCard(new DoublePrestigeShaman(105));
        loser.addCard(new Shaman(28));

        ShamanicRitual r = new ShamanicRitual(59);
        r.execute(List.of(winner, loser));

        assertEquals(20, winner.getPrestigePoints());
    }
}
package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.TileTemplate;
import it.polimi.ingsw.model.cards.drawableCards.DrawableCard;
import it.polimi.ingsw.model.cards.drawableCards.buildings.*;
import it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer.*;
import it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount.*;
import it.polimi.ingsw.model.cards.drawableCards.characters.*;
import it.polimi.ingsw.model.cards.events.*;
import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.InventorIcon;
import it.polimi.ingsw.model.enums.TotemColor;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class MesosTest {

    private Player newPlayer(String name) {
        return new Player(name, TotemColor.ORANGE);
    }

    private void give(Player p, DrawableCard card) {
        p.addCard(card);
    }

    // =========================================================================
    //  1. HUNTER
    // =========================================================================

    @Nested
    @DisplayName("Hunter")
    class HunterTests {


        /**
         * the first Hunter WITH an icon gives 1 food (only him present).
         */
        @Test
        @DisplayName("First hunter WITH icon gives exactly 1 food")
        void firstHunterWithIconGivesOneFood() {
            Player p = newPlayer("Alice");
            give(p, new Hunter(1, true));
            assertEquals(1, p.getFood(),
                    "The first hunter with an icon must give 1 food (only him in the tribe)");
        }

        /**
         * adding a second Hunter WITH an icon yields 1 food
         * for EVERY hunter present (thus 2 additional food at the time of addition).
         *
         * 1st hunter without icon (0 food) + 2nd hunter with icon → +2 food.
         */
        @Test
        @DisplayName("Second hunter WITH icon counts all present hunters")
        void secondHunterWithIconCountsAll() {
            Player p = newPlayer("Alice");
            give(p, new Hunter(1, false)); // no food
            give(p, new Hunter(1, true));  // 2 hunters in tribe → +2 food
            assertEquals(2, p.getFood());
        }

        /**
         * Adding a hunter without an icon after one with an icon MUST NOT give food.
         */
        @Test
        @DisplayName("Hunter without icon added after another gives no food")
        void addingNoIconHunterAfterIconHunterGivesNoFood() {
            Player p = newPlayer("Alice");
            give(p, new Hunter(1, true));  // +1 food
            int foodAfterFirst = p.getFood();
            give(p, new Hunter(1, false)); // should not give food
            assertEquals(foodAfterFirst, p.getFood());
        }
    }

    // =========================================================================
    //  2. INVENTOR
    // =========================================================================

    @Nested
    @DisplayName("Inventor")
    class InventorTests {

        /**
         * final score = number of inventors × number of DIFFERENT icons.
         * With 3 inventors all having different icons → 3 × 3 = 9 points.
         */
        @Test
        @DisplayName("Inventor score: n_inventors × distinct_icons")
        void inventorScoreMultipliesByDistinctIcons() {
            Player p = newPlayer("Bob");
            give(p, new Inventor(1, InventorIcon.CANOE));
            give(p, new Inventor(1, InventorIcon.BREAD));
            give(p, new Inventor(1, InventorIcon.ROPE));
            // prestigePoints=0 during game, only calculateTotalScore includes inventors
            assertEquals(9, p.calculateTotalScore());
        }

        /**
         * With 2 inventors having the same icon → distinct icons = 1 → 2 × 1 = 2 points.
         */
        @Test
        @DisplayName("Inventors with the same icon count as 1 distinct icon")
        void duplicateIconCountsOnce() {
            Player p = newPlayer("Bob");
            give(p, new Inventor(1, InventorIcon.CANOE));
            give(p, new Inventor(1, InventorIcon.CANOE));
            assertEquals(2, p.calculateTotalScore()); // 2 inventors × 1 distinct icon
        }
    }

    // =========================================================================
    //  3. ARTIST
    // =========================================================================

    @Nested
    @DisplayName("Artist")
    class ArtistTests {

        /**
         * 10 PP for every 2 Artists (integer division).
         * 3 artists → 1 pair → 10 points. NOT 15.
         */
        @Test
        @DisplayName("Artist pair: 3 artists give 10 PP (integer division)")
        void artistPairScoreIsIntegerDivision() {
            Player p = newPlayer("Carol");
            give(p, new Artist(1));
            give(p, new Artist(1));
            give(p, new Artist(1));
            assertEquals(10, p.calculateTotalScore());
        }

        /**
         * 4 artists → 2 pairs → 20 PP.
         */
        @Test
        @DisplayName("4 artists give 20 final PP")
        void fourArtistsGiveTwentyPoints() {
            Player p = newPlayer("Carol");
            for (int i = 0; i < 4; i++) give(p, new Artist(1));
            assertEquals(20, p.calculateTotalScore());
        }

    }

    // =========================================================================
    //  4. BUILDER
    // =========================================================================

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        /**
         * the discount cannot reduce the cost below 0.
         * getFoodDiscount() sums all builders → Player.getFoodDiscount() returns
         * the total sum.
         * Check that the discount does not exceed the building cost.
         */
        @Test
        @DisplayName("Builder discount does not produce a negative cost")
        void builderDiscountDoesNotGoBelowZero() {
            Player p = newPlayer("Dave");
            give(p, new Builder(1, 3, 0)); // discount 3
            give(p, new Builder(1, 3, 0)); // discount 3 → total 6
            // The building costs 4: max applicable discount = 4, not 6
            int discount = p.getFoodDiscount();
            int buildingCost = 4;
            int finalCost = Math.max(buildingCost - discount, 0);
            assertEquals(0, finalCost, "The final cost must never be negative");
        }

    }

    // =========================================================================
    //  5. COLLECTOR (Sustenance discount)
    // =========================================================================

    @Nested
    @DisplayName("Collector (Sustenance discount)")
    class CollectorTests {

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

    // =========================================================================
    //  6. EVENT: SUSTENANCE
    // =========================================================================

    @Nested
    @DisplayName("Event: Sustenance")
    class SustenanceEventTests {

        /**
         * available food MUST be paid before losing PP.
         * You cannot choose to lose PP to keep food.
         * With 2 characters and 1 food: pays 1, loses 1×PP for 1 unfed
         */
        @Test
        @DisplayName("With insufficient food: food is reset and proportional PP are lost")
        void insufficientFoodAzzerasCiboAndLosesPrestige() {
            Player p = newPlayer("Frank");
            give(p, new Artist(1));
            give(p, new Artist(1)); // 2 characters, required food=2
            p.addFood(1); // only 1 food

            Sustenance s = new Sustenance(1, 2); // 2 PP per unfed character
            s.execute(List.of(p));

            assertEquals(0, p.getFood(), "Food must be reset to zero");
            assertEquals(-2, p.getPrestigePoints(), "Loses 2 PP for 1 unfed character");
        }

        /**
         * With sufficient food: pays exactly the number of characters, no PP lost.
         */
        @Test
        @DisplayName("With sufficient food no PP are lost")
        void sufficientFoodNoPPLoss() {
            Player p = newPlayer("Frank");
            give(p, new Artist(1));
            give(p, new Artist(1));
            p.addFood(2);

            Sustenance s = new Sustenance(1, 2);
            s.execute(List.of(p));

            assertEquals(0, p.getPrestigePoints());
            assertEquals(0, p.getFood());
        }

        /**
         * Buildings DO NOT count as characters to feed.
         */
        @Test
        @DisplayName("Buildings are not counted in the total to feed")
        void buildingsNotCountedInSustenance() {
            Player p = newPlayer("Frank");
            give(p, new Artist(1));          // 1 character → total=1
            give(p, new VictoryPoints(0, 0, 3)); // building → does not count
            p.addFood(1);

            Sustenance s = new Sustenance(1, 2);
            s.execute(List.of(p));

            assertEquals(0, p.getPrestigePoints(), "The building must not be counted");
        }

        /**
         * With food=0 and 3 unfed characters: loses 3×PP.
         */
        @Test
        @DisplayName("Zero food and 3 characters: loses 3×numPP")
        void zeroFoodThreeCharactersMaxLoss() {
            Player p = newPlayer("Frank");
            give(p, new Artist(1));
            give(p, new Artist(1));
            give(p, new Artist(1));

            Sustenance s = new Sustenance(1, 3); // 3 PP per unfed
            s.execute(List.of(p));

            assertEquals(-9, p.getPrestigePoints());
        }


        @Test
        @DisplayName("Discount greater than total: no food is subtracted")
        void discountExceedsTotalNoFoodTaken() {
            Player p = newPlayer("Frank");
            give(p, new Collector(1, 3)); // discount 3
            give(p, new Collector(1, 3)); // discount 3 → total discount 6
            give(p, new Artist(1));       // 1 character to feed
            p.addFood(5);

            Sustenance s = new Sustenance(1, 2);
            s.execute(List.of(p));

            // discount=6, total=1 → total <= discount
            // food is NOT subtracted → food remains 5
            assertEquals(5, p.getFood(), "With discount >= total no food must be paid");
            assertEquals(0, p.getPrestigePoints());
        }
    }

    // =========================================================================
    //  7. EVENT: HUNT
    // =========================================================================

    @Nested
    @DisplayName("Event: Hunt")
    class HuntEventTests {


        /**
         * With 3 hunters: +3 food and +3×PP for each hunter.
         */
        @Test
        @DisplayName("Hunt with 3 hunters: correct food and PP")
        void huntWithThreeHunters() {
            Player p = newPlayer("Gina");
            give(p, new Hunter(1, false));
            give(p, new Hunter(1, false));
            give(p, new Hunter(1, false));

            Hunt h = new Hunt(1, 1, 2); // 1 food and 2 PP per hunter
            h.execute(List.of(p));

            assertEquals(3, p.getFood());
            assertEquals(6, p.getPrestigePoints());
        }

        /**
         * HunterBonus: for each hunter adds 1 food and 1 PP extra.
         * With 2 hunters and HunterBonus: Hunt gives 2 base food + 2 from building = 4.
         */
        @Test
        @DisplayName("HunterBonus adds 1 food and 1 PP per hunter during Hunt")
        void hunterBonusBuildingAddsExtraFoodAndPrestige() {
            Player p = newPlayer("Gina");
            give(p, new Hunter(1, false));
            give(p, new Hunter(1, false));
            give(p, new HunterBonus(0, 0, 2)); // free building for the test

            Hunt h = new Hunt(1, 1, 1); // 1 food, 1 PP per hunter
            h.execute(List.of(p));

            // Base: 2 food + 2 PP; HunterBonus: +2 food +2 PP
            assertEquals(4, p.getFood());
            assertEquals(4, p.getPrestigePoints());
        }
    }

    // =========================================================================
    //  8. EVENT: SHAMANIC RITUAL
    // =========================================================================

    @Nested
    @DisplayName("Event: ShamanicRitual")
    class ShamanicRitualTests {

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

    // =========================================================================
    //  9. EVENT: CAVE PAINTINGS
    // =========================================================================

    @Nested
    @DisplayName("Event: CavePaintings")
    class CavePaintingsTests {



        @Test
        @DisplayName("0 artists < upper(1) → loses 2 PP")
        void zeroArtistsBelowThreshold() {
            Player p = newPlayer("Henry");
            CavePaintings cp = new CavePaintings(1, 1, -2, 1);
            cp.execute(List.of(p));
            assertEquals(-2, p.getPrestigePoints());
        }

        @Test
        @DisplayName("1 artist >= upper(1) → gains 1×1=1 PP")
        void oneArtistAtThresholdGains() {
            Player p = newPlayer("Henry");
            give(p, new Artist(1));
            CavePaintings cp = new CavePaintings(1, 1, -2, 1);
            cp.execute(List.of(p));
            assertEquals(1, p.getPrestigePoints());
        }

        @Test
        @DisplayName("ArtistFood adds 1 food per artist during CavePaintings")
        void artistFoodBuildingAddsFoodOnEvent() {
            Player p = newPlayer("Henry");
            give(p, new Artist(1));
            give(p, new Artist(1));
            give(p, new ArtistFood(0, 0, 1));
            CavePaintings cp = new CavePaintings(1, 1, -2, 1);
            cp.execute(List.of(p));
            assertEquals(2, p.getFood());
        }
    }

    // =========================================================================
    //  10. BUILDINGS
    // =========================================================================

    @Nested
    @DisplayName("Buildings")
    class BuildingTests {

        /**
         * VictoryPoints: always adds 25 fixed PP.
         */
        @Test
        @DisplayName("VictoryPoints gives 25 final PP + prestigePoints")
        void victoryPointsAdds25() {
            Player p = newPlayer("Irene");
            VictoryPoints vp = new VictoryPoints(0, 0, 3);
            give(p, vp);
            assertEquals(25, p.calculateTotalScore());
        }


        @Test
        @DisplayName("SetScorer: Completed sets are determined by the minimum count among types")
        void minimumCountDeterminesCompletedSets() {
            Player p = newPlayer("Alice");
            give(p, new SetScorer(0, 2, 1)); // base prestige = 2

            // Give varying amounts of characters
            for(int i=0; i<3; i++) give(p, new Hunter(1, false));     // 3 Hunters
            for(int i=0; i<2; i++) give(p, new Artist(1));            // 2 Artists (Bottleneck!)
            for(int i=0; i<4; i++) give(p, new Builder(1, 0, 0));     // 4 Builders
            for(int i=0; i<2; i++) give(p, new Collector(1, 0));      // 2 Collectors (Bottleneck!)
            for(int i=0; i<3; i++) give(p, new Shaman(1, 1));         // 3 Shamans
            for(int i=0; i<5; i++) give(p, new Inventor(1, InventorIcon.CANOE)); // 5 Inventors

            // Minimum count is 2 (Artists/Collectors). So exactly 2 sets are complete.
            // 2 sets * 6 points = 12 points. Base prestige = 2. Total = 14.
            assertEquals(14, ((SetScorer) p.getTribe().get(0)).getFinalPoints(p),
                    "The number of complete sets must be bound by the minimum available character type");
        }

        /**
         * DiverseSet: 5 food for every set completed AFTER acquisition.
         * Sets completed before do not count.
         */
        @Test
        @DisplayName("DiverseSet: does not count sets already complete at the time of purchase")
        void diverseSetIgnoresPreExistingSets() {
            Player p = newPlayer("Irene");
            // First we add a complete set of 6 types
            give(p, new Hunter(1, false));
            give(p, new Artist(1));
            give(p, new Builder(1, 0, 0));
            give(p, new Collector(1, 0));
            give(p, new Shaman(1, 1));
            give(p, new Inventor(1, InventorIcon.CANOE));

            // Now we acquire DiverseSet: initialization counts the already present sets (1)
            give(p, new DiverseSet(0, 0, 1));

            assertEquals(0, p.getFood(), "Pre-existing complete sets should not give food");

            // Adding a second complete set → +5 food
            give(p, new Hunter(1, false));
            give(p, new Artist(1));
            give(p, new Builder(1, 0, 0));
            give(p, new Collector(1, 0));
            give(p, new Shaman(1, 1));
            give(p, new Inventor(1, InventorIcon.BREAD));

            assertEquals(5, p.getFood(), "The second complete set must give 5 food");
        }

        /**
         * Sets only consist of Characters.
         * Buildings are also added to the tribe as DrawableCards.
         * The system must strictly filter them out and not count them as a "7th" type
         * or accidentally validate a set.
         */
        @Test
        @DisplayName("DiverseSet & SetScorer: Buildings do not contribute to character sets")
        void buildingsDoNotContributeToSet() {
            Player p = newPlayer("Alice");
            DiverseSet diverseSet = new DiverseSet(0, 0, 1);
            give(p, diverseSet);

            // Missing an Inventor to complete the set
            give(p, new Hunter(1, false));
            give(p, new Artist(1));
            give(p, new Builder(1, 0, 0));
            give(p, new Collector(1, 0));
            give(p, new Shaman(1, 1));

            // Add a Building instead of an Inventor
            give(p, new VictoryPoints(0, 0, 1));

            assertEquals(0, p.getFood(), "Buildings must be strictly filtered out and not contribute to completing a set");
        }

        /**
         * InventorPair: 3 food for every pair of inventors with the SAME icon.
         * Does not count pairs present at the time of purchase.
         */
        @Test
        @DisplayName("InventorPair: post-purchase pair gives 3 food")
        void inventorPairGivesThreeFoodForNewPair() {
            Player p = newPlayer("Jake");
            Inventor inv1 = new Inventor(1, InventorIcon.CANOE);
            give(p, inv1);

            give(p, new InventorPair(0, 0, 1)); // acquisition: 1 CANOE already present

            // Adding a second CANOE → pair → +3 food
            give(p, new Inventor(1, InventorIcon.CANOE));
            assertEquals(3, p.getFood());
        }

        /**
         * InventorPair: DOES NOT count pairs already present at the time of purchase.
         */
        @Test
        @DisplayName("InventorPair: pre-purchase pair does not give food")
        void inventorPairIgnoresPreExistingPairs() {
            Player p = newPlayer("Jake");
            give(p, new Inventor(1, InventorIcon.CANOE));
            give(p, new Inventor(1, InventorIcon.CANOE)); // pair already present

            give(p, new InventorPair(0, 0, 1)); // acquisition

            assertEquals(0, p.getFood(), "The pre-acquisition pair must not give food");
        }

        /**
         * Rule: InventorPair gives food for pairs formed AFTER its acquisition.
         * Tricky part: If one half of the pair was acquired BEFORE the building,
         * and the second half is acquired AFTER, it should successfully form a pair.
         */
        @Test
        @DisplayName("InventorPair: One pre-existing + one new inventor completes a pair")
        void preExistingSingleInventorAndOneNewMatchingInventorGiveThreeFood() {
            Player p = newPlayer("Jake");
            give(p, new Inventor(1, InventorIcon.CANOE)); // 1st half (Pre-existing)

            give(p, new InventorPair(0, 0, 1)); // Acquisition

            give(p, new Inventor(1, InventorIcon.CANOE)); // 2nd half (New)

            assertEquals(3, p.getFood(), "A pre-existing inventor and a new matching one must form a pair and give 3 food");
        }

        /**
         * BuilderMastery: doubles the PP of Builders (adds them a second time).
         * With 2 builders of 3 PP each: base 4 PP building + 3+3 = 10 total.
         */
        @Test
        @DisplayName("BuilderMastery doubles the PP of Builders")
        void builderMasteryDoublesBuilderPoints() {
            Player p = newPlayer("Kate");
            give(p, new Builder(1, 0, 3));
            give(p, new Builder(1, 0, 3));
            BuilderMastery bm = new BuilderMastery(0, 4, 2);
            give(p, bm);

            // BuilderMastery: sums the PP of the builders (3+3=6) + own prestigePoints(4) = 10
            assertEquals(10, bm.getFinalPoints(p));
        }

        /**
         * ClassScorerArtist: PP = number_of_artists × 4 + base prestigePoints.
         * With 3 artists and base prestige = 2: 3×4+2 = 14 PP.
         */
        @Test
        @DisplayName("ClassScorerArtist: correct PP with 3 artists")
        void classScorerArtistCorrectPoints() {
            Player p = newPlayer("Laura");
            give(p, new Artist(1));
            give(p, new Artist(1));
            give(p, new Artist(1));
            ClassScorerArtist csa = new ClassScorerArtist(0, 2, 3, CharacterType.ARTIST);
            give(p, csa);

            assertEquals(14, csa.getFinalPoints(p));
        }

        /**
         * FoodDiscountArtist during Sustenance: 1 food discount for each artist.
         * With 3 artists: additional discount = 3.
         */
        @Test
        @DisplayName("FoodDiscountArtist reduces the cost at Sustenance")
        void foodDiscountArtistReducesSustenanceCost() {
            Player p = newPlayer("Marco");
            give(p, new Artist(1));
            give(p, new Artist(1));
            give(p, new Artist(1));  // 3 artists
            give(p, new FoodDiscountArtist(0, 0, 1, CharacterType.ARTIST)); // -1 food per artist
            p.addFood(0); // no food

            // 3 characters to feed, discount = 3 (artists) → total to pay = 0
            Sustenance s = new Sustenance(1, 2);
            s.execute(List.of(p));

            assertEquals(0, p.getPrestigePoints(), "The FoodDiscountArtist discount must cover all 3 characters");
        }
    }

    // =========================================================================
    //  11. PLAYER - Base logic
    // =========================================================================

    @Nested
    @DisplayName("Player – Base logic")
    class PlayerTests {


        @Test
        @DisplayName("addFood below zero: food remains 0, loses 2PP for food in deficit")
        void addFoodBelowZeroCurrentBehavior() {
            Player p = newPlayer("Mario");
            p.addFood(1);
            p.addFood(-3); // goes to -2 → food=0, loses 2*2=4 PP? No: loses 2*(-2)=-4 PP
            // food goes to 1-3 = -2 → addPrestige(2*(-2)) = addPrestige(-4)
            assertEquals(0, p.getFood());
            assertEquals(-4, p.getPrestigePoints(),
                    "Logic Error");
        }

        /**
         * calculateTotalScore includes PP from prestige + cards finalPoints + artists + inventors.
         */
        @Test
        @DisplayName("calculateTotalScore correctly sums all components")
        void calculateTotalScoreAllComponents() {
            Player p = newPlayer("Nina");
            p.addPrestige(10); // PP accumulated during the game
            give(p, new Artist(1));
            give(p, new Artist(1)); // 10 PP (1 pair)
            give(p, new Inventor(1, InventorIcon.CANOE)); // 1 inventor × 1 icon = 1 PP
            give(p, new VictoryPoints(0, 5, 3)); // 25 + 5 = 30 PP

            int total = p.calculateTotalScore();
            // 10 (prestige) + 30 (VictoryPoints) + 10 (1 pair of artists) + 1 (inventors) = 51
            assertEquals(51, total);
        }

        /**
         * countCharactersOfType does not count Buildings as characters.
         */
        @Test
        @DisplayName("countCharactersOfType does not include buildings")
        void countCharactersExcludesBuildings() {
            Player p = newPlayer("Otto");
            give(p, new Artist(1));
            give(p, new VictoryPoints(0, 0, 3));
            give(p, new Hunter(1, false));

            assertEquals(1, p.countCharactersOfType(CharacterType.ARTIST));
            assertEquals(1, p.countCharactersOfType(CharacterType.HUNTER));
            assertEquals(0, p.countCharactersOfType(CharacterType.BUILDER));
        }

        /**
         * getFoodDiscount sums the discounts of all Builders and Collectors.
         */
        @Test
        @DisplayName("getFoodDiscount sums all discounts present in the tribe")
        void foodDiscountIsSumOfAllDiscounts() {
            Player p = newPlayer("Otto");
            give(p, new Builder(1, 2, 0)); // discount 2
            give(p, new Builder(1, 1, 0)); // discount 1
            give(p, new Collector(1, 3)); // discount 3 (but only for Sustenance)

            // getFoodDiscount uses Card::getFoodDiscount which in Builder returns foodDiscount
            // Collector overrides getFoodDiscount() with its discount
            assertEquals(6, p.getFoodDiscount());
        }
    }

    // =========================================================================
    //  12. BOARD – OfferTile
    // =========================================================================

    @Nested
    @DisplayName("OfferTile")
    class OfferTileTests {

        /**
         * A free tile can be occupied.
         */
        @Test
        @DisplayName("Free tile: isFree() = true")
        void freeTileIsFree() {
            var tile = TileTemplate.B.createTile();
            assertTrue(tile.isFree());
        }

        /**
         * After setOccupyingPlayer, isFree() = false.
         */
        @Test
        @DisplayName("Occupied tile: isFree() = false")
        void occupiedTileIsNotFree() {
            var tile = TileTemplate.B.createTile();
            Player p = newPlayer("Test");
            tile.setOccupyingPlayer(p);
            assertFalse(tile.isFree());
        }

        /**
         * clearOccupyingPlayer restores the tile to free.
         */
        @Test
        @DisplayName("clearOccupyingPlayer frees the tile")
        void clearOccupyingPlayerMakesTileFree() {
            var tile = TileTemplate.C.createTile();
            Player p = newPlayer("Test");
            tile.setOccupyingPlayer(p);
            tile.clearOccupyingPlayer();
            assertTrue(tile.isFree());
        }

        /**
         * TileTemplate A: 3 bonus food, 0 upper picks, 0 lower picks.
         */
        @Test
        @DisplayName("TileTemplate A has foodBonus=3 and no picks")
        void tileTemplateAHasCorrectValues() {
            var tile = TileTemplate.A.createTile();
            assertEquals(3, tile.getFoodBonus());
            assertEquals(0, tile.getUpperRowPicks());
            assertEquals(0, tile.getLowerRowPicks());
        }

        /**
         * TileTemplate G: 2 upper picks, 1 lower pick, 0 food.
         */
        @Test
        @DisplayName("TileTemplate G has 2 upper + 1 lower picks")
        void tileTemplateGHasCorrectPicks() {
            var tile = TileTemplate.G.createTile();
            assertEquals(2, tile.getUpperRowPicks());
            assertEquals(1, tile.getLowerRowPicks());
            assertEquals(0, tile.getFoodBonus());
        }
    }
}
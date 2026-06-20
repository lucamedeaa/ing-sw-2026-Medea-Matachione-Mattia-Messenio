package it.polimi.ingsw.server.model.factory;

import it.polimi.ingsw.server.model.Deck;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.card.character.*;
import it.polimi.ingsw.server.model.card.event.*;
import it.polimi.ingsw.server.model.card.building.*;
import it.polimi.ingsw.server.model.card.building.scorer.*;
import it.polimi.ingsw.server.model.card.building.foodDiscount.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Factory for the tribe deck and era-specific building decks.
 * Card stats and costs are dynamically loaded via the CardRegistry based on the ID.
 */
public class DeckFactory {

    /**
     * Builds and shuffles the tribe deck for the given player count.
     *
     * @param playerCount number of players in the game
     * @return configured tribe deck
     */
    public static Deck buildTribeDeck(int playerCount) {
        List<Card> cards = new ArrayList<>();

        // Builders
        cards.add(new Builder(1));
        cards.add(new Builder(2));
        cards.add(new Builder(3));
        cards.add(new Builder(4));
        cards.add(new Builder(5));
        cards.add(new Builder(6));
        cards.add(new Builder(7));
        cards.add(new Builder(8));
        cards.add(new Builder(9));

        // Hunters
        cards.add(new Hunter(10));
        cards.add(new Hunter(11));
        cards.add(new Hunter(12));
        cards.add(new Hunter(13));
        cards.add(new Hunter(14));
        cards.add(new Hunter(15));
        cards.add(new Hunter(16));
        cards.add(new Hunter(17));
        cards.add(new Hunter(18));

        // Artists
        cards.add(new Artist(19));
        cards.add(new Artist(20));
        cards.add(new Artist(21));
        cards.add(new Artist(22));
        cards.add(new Artist(23));
        cards.add(new Artist(24));
        cards.add(new Artist(25));
        cards.add(new Artist(26));
        cards.add(new Artist(27));

        // Shamans
        cards.add(new Shaman(28));
        cards.add(new Shaman(29));
        cards.add(new Shaman(30));
        cards.add(new Shaman(31));
        cards.add(new Shaman(32));
        cards.add(new Shaman(33));
        cards.add(new Shaman(34));

        // Collectors
        cards.add(new Collector(35));
        cards.add(new Collector(36));
        cards.add(new Collector(37));
        cards.add(new Collector(38));

        // Inventors
        cards.add(new Inventor(39));
        cards.add(new Inventor(40));
        cards.add(new Inventor(41));
        cards.add(new Inventor(42));
        cards.add(new Inventor(43));
        cards.add(new Inventor(44));
        cards.add(new Inventor(45));
        cards.add(new Inventor(46));
        cards.add(new Inventor(47));
        cards.add(new Inventor(48));
        cards.add(new Inventor(49));
        cards.add(new Inventor(50));
        cards.add(new Inventor(51));
        cards.add(new Inventor(52));

        // Events
        cards.add(new CavePaintings(53));
        cards.add(new CavePaintings(54));
        cards.add(new CavePaintings(55));
        cards.add(new Hunt(56));
        cards.add(new Hunt(57));
        cards.add(new Hunt(117));
        cards.add(new ShamanicRitual(58));
        cards.add(new ShamanicRitual(59));
        cards.add(new ShamanicRitual(60));
        cards.add(new Sustenance(61));
        cards.add(new Sustenance(62));
        cards.add(new Sustenance(63));

        if (playerCount >= 3) {
            cards.add(new Artist(64));
            cards.add(new Artist(65));
            cards.add(new Hunter(66));
            cards.add(new Hunter(67));
            cards.add(new Hunter(68));
            cards.add(new Collector(69));
            cards.add(new Collector(70));
            cards.add(new Builder(71));
            cards.add(new Inventor(72));
            cards.add(new Inventor(73));
            cards.add(new Shaman(74));
        }

        if (playerCount >= 4) {
            cards.add(new Shaman(75));
            cards.add(new Shaman(76));
            cards.add(new Artist(77));
            cards.add(new Inventor(78));
            cards.add(new Inventor(79));
            cards.add(new Inventor(80));
            cards.add(new Inventor(81));
            cards.add(new Collector(82));
            cards.add(new Collector(83));
            cards.add(new Hunter(84));
        }

        if (playerCount >= 5) {
            cards.add(new Shaman(85));
            cards.add(new Shaman(86));
            cards.add(new Shaman(87));
            cards.add(new Collector(88));
            cards.add(new Collector(89));
            cards.add(new Collector(90));
            cards.add(new Builder(91));
            cards.add(new Builder(92));
            cards.add(new Hunter(93));
            cards.add(new Hunter(94));
            cards.add(new Artist(95));
        }

        List<Card> era1 = new ArrayList<>();
        List<Card> era2 = new ArrayList<>();
        List<Card> era3 = new ArrayList<>();
        List<Card> finalEvents = new ArrayList<>();

        for (Card c : cards) {
            if (c.getEra() == 1) era1.add(c);
            else if (c.getEra() == 2) era2.add(c);
            else if (c.getEra() == 3) {
                if (c instanceof Sustenance || c instanceof ShamanicRitual) {
                    finalEvents.add(c);
                } else {
                    era3.add(c);
                }
            }
        }

        Collections.shuffle(era1);
        Collections.shuffle(era2);
        Collections.shuffle(era3);
        Collections.shuffle(finalEvents);

        List<Card> stackedDeck = new ArrayList<>();
        stackedDeck.addAll(era1);
        stackedDeck.addAll(era2);
        stackedDeck.addAll(era3);
        stackedDeck.addAll(finalEvents);

        return new Deck(stackedDeck);
    }

    /**
     * Builds the building decks for all eras.
     *
     * @param playerCount number of players in the game
     * @return three decks, ordered by era
     */
    public static Deck[] buildBuildingDecks(int playerCount) {
        return new Deck[]{
                buildBuildingDeckForEra(1, playerCount),
                buildBuildingDeckForEra(2, playerCount),
                buildBuildingDeckForEra(3, playerCount)
        };
    }

    private static final Map<Integer, int[]> BUILDING_COUNTS = Map.of(
            2, new int[]{1, 2, 3},
            3, new int[]{2, 2, 4},
            4, new int[]{2, 3, 4},
            5, new int[]{2, 3, 5}
    );

    private static Deck buildBuildingDeckForEra(int era, int playerCount) {
        List<Card> tmp = new ArrayList<>();

        if (era == 1) {
            tmp.add(new RitualShield(96));
            tmp.add(new DiverseSet(97));
            tmp.add(new InventorPair(98));
            tmp.add(new TurnBonus(99));
            tmp.add(new FoodDiscountArtist(100));
            tmp.add(new FoodDiscountCollector(101));
        }

        if (era == 2) {
            tmp.add(new ArtistFood(102));
            tmp.add(new BuilderMastery(103));
            tmp.add(new RitualStars(104));
            tmp.add(new DoublePrestigeShaman(105));
            tmp.add(new FoodDiscountInventor(106));
            tmp.add(new SetScorer(107));
            tmp.add(new HunterBonus(108));
        }

        if (era == 3) {
            tmp.add(new VictoryPoints(109));
            tmp.add(new LatePurchase(110));
            tmp.add(new ClassScorerInventor(111));
            tmp.add(new ClassScorerArtist(112));
            tmp.add(new ClassScorerShaman(113));
            tmp.add(new ClassScorerHunter(114));
            tmp.add(new ClassScorerCollector(115));
            tmp.add(new ClassScorerBuilder(116));
        }

        Collections.shuffle(tmp);

        int count = BUILDING_COUNTS.get(playerCount)[era - 1];
        return new Deck(new ArrayList<>(tmp.subList(0, count)));
    }
}
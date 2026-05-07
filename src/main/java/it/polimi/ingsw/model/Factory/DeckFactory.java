package it.polimi.ingsw.model.Factory;
import it.polimi.ingsw.model.Deck;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.drawableCards.characters.Builder;
import it.polimi.ingsw.model.cards.drawableCards.characters.Hunter;
import it.polimi.ingsw.model.cards.drawableCards.characters.Artist;
import it.polimi.ingsw.model.cards.drawableCards.characters.Shaman;
import it.polimi.ingsw.model.cards.drawableCards.characters.Inventor;
import it.polimi.ingsw.model.cards.drawableCards.characters.Collector;
import it.polimi.ingsw.model.cards.events.CavePaintings;
import it.polimi.ingsw.model.cards.events.Hunt;
import it.polimi.ingsw.model.cards.events.Sustenance;
import it.polimi.ingsw.model.cards.events.ShamanicRitual;
import it.polimi.ingsw.model.enums.InventorIcon;
import it.polimi.ingsw.model.cards.drawableCards.buildings.RitualShield;
import it.polimi.ingsw.model.cards.drawableCards.buildings.ArtistFood;
import it.polimi.ingsw.model.cards.drawableCards.buildings.BuilderMastery;
import it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer.ClassScorerArtist;
import it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer.ClassScorerBuilder;
import it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer.ClassScorerCollector;
import it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer.ClassScorerHunter;
import it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer.ClassScorerInventor;
import it.polimi.ingsw.model.cards.drawableCards.buildings.classScorer.ClassScorerShaman;
import it.polimi.ingsw.model.cards.drawableCards.buildings.DiverseSet;
import it.polimi.ingsw.model.cards.drawableCards.buildings.DoublePrestigeShaman;
import it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount.FoodDiscountArtist;
import it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount.FoodDiscountCollector;
import it.polimi.ingsw.model.cards.drawableCards.buildings.foodDiscount.FoodDiscountInventor;
import it.polimi.ingsw.model.cards.drawableCards.buildings.HunterBonus;
import it.polimi.ingsw.model.cards.drawableCards.buildings.InventorPair;
import it.polimi.ingsw.model.cards.drawableCards.buildings.LatePurchase;
import it.polimi.ingsw.model.cards.drawableCards.buildings. RitualStars;
import it.polimi.ingsw.model.cards.drawableCards.buildings.SetScorer;
import it.polimi.ingsw.model.cards.drawableCards.buildings.TurnBonus;
import it.polimi.ingsw.model.cards.drawableCards.buildings.VictoryPoints;
import it.polimi.ingsw.model.enums.CharacterType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DeckFactory {
    public static Deck buildTribeDeck(int playerCount) {
        List<Card> cards = new ArrayList<>();

            cards.add(new Builder(1,1,  1, 2));
            cards.add(new Builder(2,1,  2, 0));
            cards.add(new Builder(3,1,  1, 3));
            cards.add(new Builder(4,2,  1, 4));
            cards.add(new Builder(5,2,  2, 1));
            cards.add(new Builder(6,2,  2, 3));
            cards.add(new Builder(7,3,  1, 5));
            cards.add(new Builder(8,3,  2, 3));
            cards.add(new Builder(9,3,  2, 2));

            cards.add(new Hunter(10,1,  TRUE));
            cards.add(new Hunter(11,1,  TRUE));
            cards.add(new Hunter(12,1,  FALSE));
            cards.add(new Hunter(13,2,  TRUE));
            cards.add(new Hunter(14,2,  FALSE));
            cards.add(new Hunter(15,2,  FALSE));
            cards.add(new Hunter(16,3,  TRUE));
            cards.add(new Hunter(17,3,  FALSE));
            cards.add(new Hunter(18,3,  FALSE));

            cards.add(new Artist(19,1));
            cards.add(new Artist(20,1));
            cards.add(new Artist(21,1));
            cards.add(new Artist(22,2));
            cards.add(new Artist(23,2));
            cards.add(new Artist(24,2));
            cards.add(new Artist(25,3));
            cards.add(new Artist(26,3));
            cards.add(new Artist(27,3));

            cards.add(new Shaman(28,1, 1));
            cards.add(new Shaman(29,1, 2));
            cards.add(new Shaman(30,2, 2));
            cards.add(new Shaman(31,2, 2));
            cards.add(new Shaman(32,3, 2));
            cards.add(new Shaman(33,3, 3));
            cards.add(new Shaman(34,3, 3));

            cards.add(new Collector(35,1, 3));
            cards.add(new Collector(36,1, 3));
            cards.add(new Collector(37,2, 3));
            cards.add(new Collector(38,3, 3));


            cards.add(new Inventor(39,1,  InventorIcon.SPEARHEAD));
            cards.add(new Inventor(40,1,  InventorIcon.LEATHER));
            cards.add(new Inventor(41,1,  InventorIcon.BREAD));
            cards.add(new Inventor(42,1,  InventorIcon.CANOE));
            cards.add(new Inventor(43,1,  InventorIcon.MORTAR));
            cards.add(new Inventor(44,2,  InventorIcon.ROPE));
            cards.add(new Inventor(45,2,  InventorIcon.LEATHER));
            cards.add(new Inventor(46,2,  InventorIcon.MORTAR));
            cards.add(new Inventor(47,2,  InventorIcon.FLUTE));
            cards.add(new Inventor(48,2,  InventorIcon.STATUE));
            cards.add(new Inventor(49,2,  InventorIcon.FISHHOOK));
            cards.add(new Inventor(50,2,  InventorIcon.STATUE));
            cards.add(new Inventor(51,2,  InventorIcon.NECKLACE));
            cards.add(new Inventor(52,2,  InventorIcon.BREAD));

            cards.add(new CavePaintings(53,1, 1, -2, 1));
            cards.add(new CavePaintings(54,2, 2, -2, 2));
            cards.add(new CavePaintings(55,3, 3, -2, 3));


            cards.add(new Hunt(56,1, 1, 1));
            cards.add(new Hunt(57,2, 1, 2));

            cards.add(new ShamanicRitual(58,1, 5, -3));
            cards.add(new ShamanicRitual(59,2, 10, -5));
            cards.add(new ShamanicRitual(60,3, 15, -7));

            cards.add(new Sustenance(61,1, 1));
            cards.add(new Sustenance(62,2, 2));
            cards.add(new Sustenance(63,3, 3));



        if (playerCount >= 3) {
            cards.add(new Artist(64,1));
            cards.add(new Artist(65,2));


            cards.add(new Hunter(66,1, FALSE));
            cards.add(new Hunter(67,1, FALSE));
            cards.add(new Hunter(68,2, TRUE));

            cards.add(new Collector(69,1, 3));
            cards.add(new Collector(70,2, 3));

            cards.add(new Builder(71,2, 1, 2));

            cards.add(new Inventor(72,3, InventorIcon.SPEARHEAD));
            cards.add(new Inventor(73,3, InventorIcon.CANOE));

            cards.add(new Shaman(74,3, 2));
        }

        if (playerCount >= 4) {
            cards.add(new Shaman(75,1, 1));
            cards.add(new Shaman(76,3, 2));


            cards.add(new Artist(77,1));

            cards.add(new Inventor(78,1, InventorIcon.ROPE));
            cards.add(new Inventor(79,1, InventorIcon.FLUTE));
            cards.add(new Inventor(80,2, InventorIcon.FISHHOOK));
            cards.add(new Inventor(81,3, InventorIcon.NECKLACE));


            cards.add(new Collector(82,2, 3));
            cards.add(new Collector(83,3, 3));


            cards.add(new Hunter(84,2, TRUE));

        }

        if (playerCount >= 5) {
            cards.add(new Shaman(85,1, 2));
            cards.add(new Shaman(86,2, 1));
            cards.add(new Shaman(87,2, 2));

            cards.add(new Collector(88,1, 3));
            cards.add(new Collector(89,2, 3));
            cards.add(new Collector(90,3, 3));


            cards.add(new Builder(91,1, 2, 1));
            cards.add(new Builder(92,3, 1, 4));


            cards.add(new Hunter(93,2, FALSE));
            cards.add(new Hunter(94,3, TRUE));

            cards.add(new Artist(95,3));

        }
        List<Card> era1 = new ArrayList<>();
        List<Card> era2 = new ArrayList<>();
        List<Card> era3 = new ArrayList<>();

        for (Card c : cards) {
            if (c.getEra() == 1) era1.add(c);
            else if (c.getEra() == 2) era2.add(c);
            else if (c.getEra() == 3) era3.add(c);
        }

        Collections.shuffle(era1);
        Collections.shuffle(era2);
        Collections.shuffle(era3);

        List<Card> stackedDeck = new ArrayList<>();
        stackedDeck.addAll(era1);
        stackedDeck.addAll(era2);
        stackedDeck.addAll(era3);

        return new Deck(stackedDeck);
    }

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
        List<Card> tmp= new ArrayList<>();



        //to manage the deck shuffle based on the number of players
        if(era == 1){
            tmp.add(new RitualShield(96,5, 2, 1));
            tmp.add(new DiverseSet(97,4, 3 ,1));
            tmp.add(new InventorPair(98,3, 4, 1));
            tmp.add(new TurnBonus(99,3, 3, 1));
            tmp.add(new FoodDiscountArtist(100,5, 3, 1, CharacterType.ARTIST));
            tmp.add(new FoodDiscountCollector(101,4, 4, 1, CharacterType.COLLECTOR));
        }

        if(era == 2){
            tmp.add(new ArtistFood(102,5, 6, 2));
            tmp.add(new BuilderMastery(103,6, 4 ,2));
            tmp.add(new RitualStars(104,6, 4, 2));
            tmp.add(new DoublePrestigeShaman(105,7, 0, 2));
            tmp.add(new FoodDiscountInventor(106,7, 4, 2, CharacterType.INVENTOR));
            tmp.add(new SetScorer(107,5, 6, 2));
            tmp.add(new HunterBonus(108,7, 2, 2 ));
        }
        if(era == 3){
            tmp.add(new VictoryPoints(109,10, 0, 3));
            tmp.add(new LatePurchase(110,9, 3 ,3));
            tmp.add(new ClassScorerInventor(111,6, 6, 3, CharacterType.INVENTOR));
            tmp.add(new ClassScorerArtist(112,7, 4, 3,CharacterType.ARTIST));
            tmp.add(new ClassScorerShaman(113,7, 4, 3,CharacterType.SHAMAN));
            tmp.add(new ClassScorerHunter(114,8, 8, 3,CharacterType.HUNTER));
            tmp.add(new ClassScorerCollector(115,7, 6, 3,CharacterType.COLLECTOR));
            tmp.add(new ClassScorerBuilder(116,6, 3, 3,CharacterType.BUILDER));
        }

        Collections.shuffle(tmp);

        int count = BUILDING_COUNTS.get(playerCount)[era - 1];
        return new Deck(new ArrayList<>(tmp.subList(0, count)));

    }
}

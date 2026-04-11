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
import it.polimi.ingsw.model.cards.drawableCards.buildings.ClassScorer;
import it.polimi.ingsw.model.cards.drawableCards.buildings.DiverseSet;
import it.polimi.ingsw.model.cards.drawableCards.buildings.DoublePrestigeShaman;
import it.polimi.ingsw.model.cards.drawableCards.buildings.FoodDiscount;
import it.polimi.ingsw.model.cards.drawableCards.buildings.HunterBonus;
import it.polimi.ingsw.model.cards.drawableCards.buildings.InventorPair;
import it.polimi.ingsw.model.cards.drawableCards.buildings.LatePurchase;
import it.polimi.ingsw.model.cards.drawableCards.buildings. RitualStars;
import it.polimi.ingsw.model.cards.drawableCards.buildings.SetScorer;
import it.polimi.ingsw.model.cards.drawableCards.buildings.TurnBonus;
import it.polimi.ingsw.model.cards.drawableCards.buildings.VictoryPoints;

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

            cards.add(new Builder(1,  1, 2));
            cards.add(new Builder(1,  2, 0));
            cards.add(new Builder(1,  1, 3));
            cards.add(new Builder(2,  1, 4));
            cards.add(new Builder(2,  2, 1));
            cards.add(new Builder(2,  2, 3));
            cards.add(new Builder(3,  1, 5));
            cards.add(new Builder(3,  2, 3));
            cards.add(new Builder(3,  2, 2));

            cards.add(new Hunter(1,  TRUE));
            cards.add(new Hunter(1,  TRUE));
            cards.add(new Hunter(1,  FALSE));
            cards.add(new Hunter(2,  TRUE));
            cards.add(new Hunter(2,  FALSE));
            cards.add(new Hunter(2,  FALSE));
            cards.add(new Hunter(3,  TRUE));
            cards.add(new Hunter(3,  FALSE));
            cards.add(new Hunter(3,  FALSE));

            cards.add(new Artist(1));
            cards.add(new Artist(1));
            cards.add(new Artist(1));
            cards.add(new Artist(2));
            cards.add(new Artist(2));
            cards.add(new Artist(2));
            cards.add(new Artist(3));
            cards.add(new Artist(3));
            cards.add(new Artist(3));

            cards.add(new Shaman(1, 1));
            cards.add(new Shaman(1, 2));
            cards.add(new Shaman(2, 2));
            cards.add(new Shaman(2, 2));
            cards.add(new Shaman(3, 2));
            cards.add(new Shaman(3, 3));
            cards.add(new Shaman(3, 3));

            cards.add(new Collector(1, 3));
            cards.add(new Collector(1, 3));
            cards.add(new Collector(2, 3));
            cards.add(new Collector(3, 3));


            cards.add(new Inventor(1,  InventorIcon.SPEARHEAD));
            cards.add(new Inventor(1,  InventorIcon.LEATHER));
            cards.add(new Inventor(1,  InventorIcon.BREAD));
            cards.add(new Inventor(1,  InventorIcon.CANOE));
            cards.add(new Inventor(1,  InventorIcon.MORTAR));
            cards.add(new Inventor(2,  InventorIcon.ROPE));
            cards.add(new Inventor(2,  InventorIcon.LEATHER));
            cards.add(new Inventor(2,  InventorIcon.MORTAR));
            cards.add(new Inventor(2,  InventorIcon.FLUTE));
            cards.add(new Inventor(2,  InventorIcon.STATUE));
            cards.add(new Inventor(2,  InventorIcon.FISHHOOK));
            cards.add(new Inventor(2,  InventorIcon.STATUE));
            cards.add(new Inventor(2,  InventorIcon.NECKLACE));
            cards.add(new Inventor(2,  InventorIcon.BREAD));

            cards.add(new CavePaintings(1, 1,  2, 1));
            cards.add(new CavePaintings(2, 2,  2, 2));

            cards.add(new Hunt(1, 1, 1));
            cards.add(new Hunt(2, 1, 2));

            cards.add(new ShamanicRitual(1, 5, 3));
            cards.add(new ShamanicRitual(2, 10, 5));
            cards.add(new ShamanicRitual(3, 15, 7));

            cards.add(new Sustenance(1, 1));
            cards.add(new Sustenance(2, 2));
            cards.add(new Sustenance(2, 2));



        if (playerCount >= 3) {
            cards.add(new Artist(1));
            cards.add(new Artist(2));


            cards.add(new Hunter(1, FALSE));
            cards.add(new Hunter(1, FALSE));
            cards.add(new Hunter(2, TRUE));

            cards.add(new Collector(1, 3));
            cards.add(new Collector(2, 3));

            cards.add(new Builder(2, 1, 2));

            cards.add(new Inventor(3, InventorIcon.SPEARHEAD));
            cards.add(new Inventor(3, InventorIcon.CANOE));

            cards.add(new Shaman(3, 2));
        }

        if (playerCount >= 4) {
            cards.add(new Shaman(1, 1));
            cards.add(new Shaman(3, 2));


            cards.add(new Artist(1));

            cards.add(new Inventor(1, InventorIcon.ROPE));
            cards.add(new Inventor(1, InventorIcon.FLUTE));
            cards.add(new Inventor(2, InventorIcon.FISHHOOK));
            cards.add(new Inventor(3, InventorIcon.NECKLACE));


            cards.add(new Collector(2, 3));
            cards.add(new Collector(3, 3));


            cards.add(new Hunter(2, TRUE));

        }

        if (playerCount >= 5) {
            cards.add(new Shaman(1, 2));
            cards.add(new Shaman(2, 1));
            cards.add(new Shaman(2, 2));

            cards.add(new Collector(1, 3));
            cards.add(new Collector(2, 3));
            cards.add(new Collector(3, 3));


            cards.add(new Builder(1, 2, 1));
            cards.add(new Builder(3, 1, 4));


            cards.add(new Hunter(2, FALSE));
            cards.add(new Hunter(3, TRUE));

            cards.add(new Artist(3));

        }
        return new Deck(cards);
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
            tmp.add(new RitualShield(5, 2, 1));
            tmp.add(new DiverseSet(4, 3 ,1));
            tmp.add(new InventorPair(3, 4, 1));
            tmp.add(new TurnBonus(3, 3, 1));
            tmp.add(new FoodDiscount(5, 3, 1, ARTIST));
            tmp.add(new FoodDiscount(4, 4, 1, COLLECTOR));
        }

        if(era == 2){
            tmp.add(new ArtistFood(5, 6, 2));
            tmp.add(new BuilderMastery(6, 4 ,2));
            tmp.add(new RitualStars(6, 4, 2));
            tmp.add(new DoublePrestigeShaman(7, 0, 2));
            tmp.add(new FoodDiscount(7, 4, 2, INVENTOR));
            tmp.add(new SetScorer(5, 6, 2));
            tmp.add(new HunterBonus(7, 2, 2 ));
        }
        if(era == 3){
            tmp.add(new VictoryPoints(10, 0, 3));
            tmp.add(new LatePurchase(9, 3 ,3));
            tmp.add(new ClassScorer(6, 6, 3, INVENTOR));
            tmp.add(new ClassScorer(7, 4, 3, ARTIST));
            tmp.add(new ClassScorer(7, 4, 3, SHAMAN));
            tmp.add(new ClassScorer(8, 8, 3, HUNTER));
            tmp.add(new ClassScorer(7, 6, 3, COLLECTOR));
            tmp.add(new ClassScorer(6, 3, 3, BUILDER));
        }

        Collections.shuffle(tmp);

        int count = BUILDING_COUNTS.get(playerCount)[era - 1];
        return new Deck(new ArrayList<>(tmp.subList(0, count)));

    }
}

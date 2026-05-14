package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Deck;
import it.polimi.ingsw.server.model.card.character.Artist;
import it.polimi.ingsw.server.model.card.character.Hunter;
import it.polimi.ingsw.server.model.card.character.Shaman;
import it.polimi.ingsw.server.model.card.Card;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest extends ModelTest {


    private Deck deckOf(Card... cards) {
        return new Deck(new ArrayList<>(List.of(cards)));
    }


    @Nested
    @DisplayName("isEmpty")
    class IsEmpty {

        @Test
        @DisplayName("empty deck returns true")
        void emptyDeckIsEmpty() {
            assertTrue(new Deck(new ArrayList<>()).isEmpty());
        }

        @Test
        @DisplayName("non-empty deck returns false")
        void nonEmptyDeckIsNotEmpty() {
            assertFalse(deckOf(new Artist(19)).isEmpty());
        }

        @Test
        @DisplayName("becomes empty after drawing all cards")
        void emptyAfterDrawingAll() {
            Deck d = deckOf(new Artist(19));
            d.draw();
            assertTrue(d.isEmpty());
        }
    }


    @Nested
    @DisplayName("size")
    class Size {

        @Test
        @DisplayName("empty deck has size 0")
        void emptyDeckSizeZero() {
            assertEquals(0, new Deck(new ArrayList<>()).size());
        }

        @Test
        @DisplayName("deck size matches number of cards given")
        void sizeMatchesCardCount() {
            Deck d = deckOf(new Artist(19), new Hunter(10), new Shaman(28));
            assertEquals(3, d.size());
        }

        @Test
        @DisplayName("size decreases by 1 after each draw")
        void sizeDecreasesAfterDraw() {
            Deck d = deckOf(new Artist(19), new Artist(20));
            d.draw();
            assertEquals(1, d.size());
        }
    }


    @Nested
    @DisplayName("draw")
    class Draw {

        @Test
        @DisplayName("draw on empty deck returns null")
        void drawEmptyReturnsNull() {
            assertNull(new Deck(new ArrayList<>()).draw());
        }

        @Test
        @DisplayName("draw returns a non-null card from non-empty deck")
        void drawReturnsCard() {
            Deck d = deckOf(new Artist(19));
            assertNotNull(d.draw());
        }

        @Test
        @DisplayName("drawing all cards empties the deck")
        void drawAllEmptiesDeck() {
            Deck d = deckOf(new Artist(19), new Artist(20));
            d.draw();
            d.draw();
            assertTrue(d.isEmpty());
        }

        @Test
        @DisplayName("draw after emptying returns null")
        void drawAfterEmptyReturnsNull() {
            Deck d = deckOf(new Artist(19));
            d.draw();
            assertNull(d.draw());
        }
    }
}
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

        /**
         * SUMMARY:
         * Verifies that a newly created deck with no cards reports itself as empty.
         *
         * EXPECTATION:
         * isEmpty() returns true for a deck constructed with an empty list.
         */
        @Test
        @DisplayName("empty deck returns true")
        void emptyDeckIsEmpty() {
            assertTrue(new Deck(new ArrayList<>()).isEmpty());
        }

        /**
         * SUMMARY:
         * Verifies that a deck containing at least one card is not considered empty.
         *
         * EXPECTATION:
         * isEmpty() returns false for a deck with one card.
         */
        @Test
        @DisplayName("non-empty deck returns false")
        void nonEmptyDeckIsNotEmpty() {
            assertFalse(deckOf(new Artist(19)).isEmpty());
        }

        /**
         * SUMMARY:
         * Verifies that after drawing the only card from a single-card deck,
         * the deck becomes empty.
         *
         * EXPECTATION:
         * isEmpty() returns true after all cards have been drawn.
         */
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

        /**
         * SUMMARY:
         * Verifies that the deck size matches the exact number of cards
         * provided at construction.
         *
         * EXPECTATION:
         * size() returns 3 for a deck created with three cards.
         */
        @Test
        @DisplayName("deck size matches number of cards given")
        void sizeMatchesCardCount() {
            Deck d = deckOf(new Artist(19), new Hunter(10), new Shaman(28));
            assertEquals(3, d.size());
        }

        /**
         * SUMMARY:
         * Verifies that drawing one card reduces the deck size by exactly one.
         *
         * EXPECTATION:
         * size() returns 1 after drawing once from a two-card deck.
         */
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

        /**
         * SUMMARY:
         * Verifies that drawing from an empty deck returns null instead
         * of throwing an exception.
         *
         * EXPECTATION:
         * draw() returns null when the deck has no cards.
         */
        @Test
        @DisplayName("draw on empty deck returns null")
        void drawEmptyReturnsNull() {
            assertNull(new Deck(new ArrayList<>()).draw());
        }

        /**
         * SUMMARY:
         * Verifies that drawing from a non-empty deck returns a valid
         * (non-null) card object.
         *
         * EXPECTATION:
         * draw() returns a non-null Card from a deck with one card.
         */
        @Test
        @DisplayName("draw returns a non-null card from non-empty deck")
        void drawReturnsCard() {
            Deck d = deckOf(new Artist(19));
            assertNotNull(d.draw());
        }

        /**
         * SUMMARY:
         * Verifies that drawing from an already-emptied deck returns null,
         * confirming safe repeated draws beyond the deck's capacity.
         *
         * EXPECTATION:
         * draw() returns null after all cards have already been drawn.
         */
        @Test
        @DisplayName("draw after emptying returns null")
        void drawAfterEmptyReturnsNull() {
            Deck d = deckOf(new Artist(19));
            d.draw();
            assertNull(d.draw());
        }
    }
}
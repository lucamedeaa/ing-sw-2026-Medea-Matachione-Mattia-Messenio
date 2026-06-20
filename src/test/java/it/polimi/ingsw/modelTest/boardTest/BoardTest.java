package it.polimi.ingsw.modelTest.boardTest;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.OfferTile;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest extends ModelTest {


    private Board board(int n) {
        List<Player> players = newPlayers(n);
        return new Board(n, players);
    }

    private List<Player> players(int n) {
        return newPlayers(n);
    }

    @Nested
    @DisplayName("Board construction")
    class Construction {

        /**
         * SUMMARY:
         * Verifies that a Board can be constructed with 2 players without throwing any exception.
         *
         * EXPECTATION:
         * No exception is thrown during 2-player board initialization.
         */
        @Test
        @DisplayName("2-player board initialises without exceptions")
        void twoPlayerBoardInitialises() {
            assertDoesNotThrow(() -> board(2));
        }

        /**
         * SUMMARY:
         * Verifies that a Board can be constructed with 3 players without throwing any exception.
         *
         * EXPECTATION:
         * No exception is thrown during 3-player board initialization.
         */
        @Test
        @DisplayName("3-player board initialises without exceptions")
        void threePlayerBoardInitialises() {
            assertDoesNotThrow(() -> board(3));
        }

        /**
         * SUMMARY:
         * Verifies that a Board can be constructed with 4 players without throwing any exception.
         *
         * EXPECTATION:
         * No exception is thrown during 4-player board initialization.
         */
        @Test
        @DisplayName("4-player board initialises without exceptions")
        void fourPlayerBoardInitialises() {
            assertDoesNotThrow(() -> board(4));
        }

        /**
         * SUMMARY:
         * Verifies that a Board can be constructed with 5 players without throwing any exception.
         *
         * EXPECTATION:
         * No exception is thrown during 5-player board initialization.
         */
        @Test
        @DisplayName("5-player board initialises without exceptions")
        void fivePlayerBoardInitialises() {
            assertDoesNotThrow(() -> board(5));
        }

        /**
         * SUMMARY:
         * Verifies that constructing a Board with an invalid player count (1) throws an exception.
         *
         * EXPECTATION:
         * An IllegalArgumentException is thrown when creating a board with only 1 player.
         */
        @Test
        @DisplayName("Invalid player count throws IllegalArgumentException")
        void invalidPlayerCountThrows() {
            List<Player> p = players(1);
            assertThrows(IllegalArgumentException.class, () -> new Board(1, p));
        }

        /**
         * SUMMARY:
         * Verifies that the offer track is properly initialized after board construction.
         *
         * EXPECTATION:
         * The offer track is non-null and contains at least one tile.
         */
        @Test
        @DisplayName("offerTrack is non-null and non-empty after construction")
        void offerTrackNonEmpty() {
            Board b = board(3);
            assertNotNull(b.getOfferTrack());
            assertFalse(b.getOfferTrack().isEmpty());
        }

        /**
         * SUMMARY:
         * Verifies that all offer tiles on the track are free immediately after board construction.
         *
         * EXPECTATION:
         * Every OfferTile in the offer track returns true for isFree().
         */
        @Test
        @DisplayName("All offer tiles are free after construction")
        void allTilesFreeAtStart() {
            Board b = board(4);
            assertTrue(b.getOfferTrack().stream().allMatch(OfferTile::isFree));
        }
    }


    @Nested
    @DisplayName("Turn order (totem management)")
    class TurnOrder {

        /**
         * SUMMARY:
         * Verifies that getCurrentPlayer returns a valid player right after board construction.
         *
         * EXPECTATION:
         * getCurrentPlayer returns a non-null Player object.
         */
        @Test
        @DisplayName("getCurrentPlayer returns a non-null player at start")
        void getCurrentPlayerNonNull() {
            List<Player> ps = players(3);
            Board b = new Board(3, ps);
            assertNotNull(b.getCurrentPlayer());
        }

        /**
         * SUMMARY:
         * Verifies that getCurrentPlayer throws when all players' totems have been consumed.
         *
         * EXPECTATION:
         * An IllegalStateException is thrown when no more totems remain in the current order.
         */
        @Test
        @DisplayName("getCurrentPlayer throws when all totems consumed")
        void getCurrentPlayerThrowsWhenEmpty() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            b.consumeCurrentPlayer();
            b.consumeCurrentPlayer();
            assertThrows(IllegalStateException.class, b::getCurrentPlayer);
        }

        /**
         * SUMMARY:
         * Verifies that calling consumeCurrentPlayer advances the turn to a different player.
         *
         * EXPECTATION:
         * The current player after consuming is different from the one before consuming.
         */
        @Test
        @DisplayName("consumeCurrentPlayer advances to next player")
        void consumeCurrentPlayerAdvances() {
            List<Player> ps = players(3);
            Board b = new Board(3, ps);
            Player first = b.getCurrentPlayer();
            b.consumeCurrentPlayer();
            Player second = b.getCurrentPlayer();
            assertNotEquals(first, second);
        }

        /**
         * SUMMARY:
         * Verifies that allTotemsPlaced returns false at the start of a round before any totems are consumed.
         *
         * EXPECTATION:
         * allTotemsPlaced() returns false on a freshly constructed board.
         */
        @Test
        @DisplayName("allTotemsPlaced is false at start")
        void allTotemsPlacedFalseAtStart() {
            Board b = board(3);
            assertFalse(b.allTotemsPlaced());
        }

        /**
         * SUMMARY:
         * Verifies that allTotemsPlaced returns true once every player's totem has been consumed.
         *
         * EXPECTATION:
         * allTotemsPlaced() returns true after all players have been consumed.
         */
        @Test
        @DisplayName("allTotemsPlaced is true after consuming all players")
        void allTotemsPlacedTrueAfterAll() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            b.consumeCurrentPlayer();
            b.consumeCurrentPlayer();
            assertTrue(b.allTotemsPlaced());
        }

        /**
         * SUMMARY:
         * Verifies that returnTotem transfers a player to the next round's totem order, making them available after cleanup.
         *
         * EXPECTATION:
         * After returnTotem and cleanupForNextRound, getCurrentPlayer returns a non-null player.
         */
        @Test
        @DisplayName("returnTotem adds player to nextTotemOrder (visible via cleanupForNextRound)")
        void returnTotemTransfersToNextOrder() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            Player first = b.getCurrentPlayer();
            b.consumeCurrentPlayer();
            b.returnTotem(first);
            // After cleanup the first player is now in currentTotemOrder again
            b.cleanupForNextRound(ps);
            assertNotNull(b.getCurrentPlayer());
        }

        /**
         * SUMMARY:
         * Verifies that the first player to return their totem receives a positive food bonus.
         *
         * EXPECTATION:
         * The first returner's food increases by at least 1 compared to before returning.
         */
        @Test
        @DisplayName("returnTotem: first returner gets positive food bonus (2 players)")
        void firstReturnerGetsPositiveFoodBonus() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            Player first = b.getCurrentPlayer();
            int foodBefore = first.getFood();
            b.consumeCurrentPlayer();
            b.returnTotem(first); // index 0 → bonus +1
            assertTrue(first.getFood() >= foodBefore + 1);
        }

        /**
         * SUMMARY:
         * Verifies that the last player to return their totem receives a food penalty in a 2-player game.
         *
         * EXPECTATION:
         * The last returner's food is less than or equal to their food before returning.
         */
        @Test
        @DisplayName("returnTotem: last returner loses food (2 players)")
        void lastReturnerLosesFood() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            Player first = b.getCurrentPlayer();
            b.consumeCurrentPlayer();
            Player second = b.getCurrentPlayer();
            b.consumeCurrentPlayer();
            b.returnTotem(first);
            int foodBefore = second.getFood();
            b.returnTotem(second); // index 1 → bonus -1
            assertTrue(second.getFood() <= foodBefore);
        }
    }


    @Nested
    @DisplayName("Offer track & placeTotem")
    class OfferTrackTests {

        /**
         * SUMMARY:
         * Verifies that calling placeTotem on a free tile marks it as occupied.
         *
         * EXPECTATION:
         * The tile at the specified index is no longer free after placeTotem is called.
         */
        @Test
        @DisplayName("placeTotem occupies a free tile")
        void placeTotemOccupiesTile() {
            List<Player> ps = players(3);
            Board b = new Board(3, ps);
            Player p = ps.get(0);
            b.placeTotem(0, p);
            assertFalse(b.getOfferTrack().get(0).isFree());
        }

        /**
         * SUMMARY:
         * Verifies that placing a totem on an already-occupied tile throws an exception.
         *
         * EXPECTATION:
         * An InvalidGameActionException is thrown when a second player tries to occupy the same tile.
         */
        @Test
        @DisplayName("placeTotem on already-occupied tile throws InvalidGameActionException")
        void placeTotemOnOccupiedThrows() {
            List<Player> ps = players(3);
            Board b = new Board(3, ps);
            Player p1 = ps.get(0);
            Player p2 = ps.get(1);
            b.placeTotem(0, p1);
            assertThrows(InvalidGameActionException.class, () -> b.placeTotem(0, p2));
        }

        /**
         * SUMMARY:
         * Verifies that placeTotem rejects a negative tile index.
         *
         * EXPECTATION:
         * An InvalidGameActionException is thrown when index is -1.
         */
        @Test
        @DisplayName("placeTotem with negative index throws InvalidGameActionException")
        void placeTotemNegativeIndexThrows() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            assertThrows(InvalidGameActionException.class, () -> b.placeTotem(-1, ps.get(0)));
        }

        /**
         * SUMMARY:
         * Verifies that placeTotem rejects an index equal to the offer track size (out of bounds).
         *
         * EXPECTATION:
         * An InvalidGameActionException is thrown when the index is out of bounds.
         */
        @Test
        @DisplayName("placeTotem with out-of-bounds index throws InvalidGameActionException")
        void placeTotemOutOfBoundsThrows() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            int outOfBounds = b.getOfferTrack().size();
            assertThrows(InvalidGameActionException.class, () -> b.placeTotem(outOfBounds, ps.get(0)));
        }
    }

    // Card access (peekCard / takeCard)
    @Nested
    @DisplayName("Card access: peekCard & takeCard")
    class CardAccess {

        /**
         * SUMMARY:
         * Verifies that peekCard returns a valid card from the upper row at position (0, 0).
         *
         * EXPECTATION:
         * peekCard(0, 0) returns a non-null Card object.
         */
        @Test
        @DisplayName("peekCard(0, 0) returns a non-null card from upper row")
        void peekUpperRowReturnsCard() {
            Board b = board(2);
            assertNotNull(b.peekCard(0, 0));
        }

        /**
         * SUMMARY:
         * Verifies that peekCard returns a valid card from the lower row at position (1, 0).
         *
         * EXPECTATION:
         * peekCard(1, 0) returns a non-null Card object.
         */
        @Test
        @DisplayName("peekCard(1, 0) returns a non-null card from lower row")
        void peekLowerRowReturnsCard() {
            Board b = board(2);
            assertNotNull(b.peekCard(1, 0));
        }

        /**
         * SUMMARY:
         * Verifies that takeCard removes the card from the slot, making subsequent peeks invalid.
         *
         * EXPECTATION:
         * After takeCard(0, 0), calling peekCard(0, 0) throws InvalidGameActionException.
         */
        @Test
        @DisplayName("takeCard removes the card (second peek on same slot throws)")
        void takeCardRemovesCard() {
            Board b = board(2);
            b.takeCard(0, 0);
            assertThrows(InvalidGameActionException.class, () -> b.peekCard(0, 0));
        }

        /**
         * SUMMARY:
         * Verifies that takeCard returns the exact same Card object that was previously peeked.
         *
         * EXPECTATION:
         * The card returned by takeCard is the same instance (assertSame) as the one returned by peekCard.
         */
        @Test
        @DisplayName("takeCard returns the correct card (same object as peek)")
        void takeCardReturnsSameAspeek() {
            Board b = board(3);
            Card peeked = b.peekCard(0, 0);
            Card taken = b.takeCard(0, 0);
            assertSame(peeked, taken);
        }

        /**
         * SUMMARY:
         * Verifies that peeking a slot from which a card was already taken throws an exception.
         *
         * EXPECTATION:
         * An InvalidGameActionException is thrown when peeking a previously taken lower-row slot.
         */
        @Test
        @DisplayName("peekCard on already-taken slot throws InvalidGameActionException")
        void peekOnTakenSlotThrows() {
            Board b = board(2);
            b.takeCard(1, 0);
            assertThrows(InvalidGameActionException.class, () -> b.peekCard(1, 0));
        }
    }


    // Round cleanup
    @Nested
    @DisplayName("cleanupForNextRound")
    class RoundCleanup {

        /**
         * SUMMARY:
         * Verifies that cleanupForNextRound executes without error after all totems have been consumed and returned.
         *
         * EXPECTATION:
         * No exception is thrown when cleanupForNextRound is called after a complete round cycle.
         */
        @Test
        @DisplayName("cleanupForNextRound does not throw with a full round of returns")
        void cleanupDoesNotThrow() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            Player p1 = b.getCurrentPlayer(); b.consumeCurrentPlayer();
            Player p2 = b.getCurrentPlayer(); b.consumeCurrentPlayer();
            b.returnTotem(p1);
            b.returnTotem(p2);
            assertDoesNotThrow(() -> b.cleanupForNextRound(ps));
        }

        /**
         * SUMMARY:
         * Verifies that after cleanupForNextRound, the totem order is reset so totems are available again.
         *
         * EXPECTATION:
         * allTotemsPlaced() returns false after cleanup, indicating a new round can begin.
         */
        @Test
        @DisplayName("After cleanupForNextRound, allTotemsPlaced is false again")
        void afterCleanupTotemsAvailable() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            Player p1 = b.getCurrentPlayer(); b.consumeCurrentPlayer();
            Player p2 = b.getCurrentPlayer(); b.consumeCurrentPlayer();
            b.returnTotem(p1);
            b.returnTotem(p2);
            b.cleanupForNextRound(ps);
            assertFalse(b.allTotemsPlaced());
        }
    }

    @Nested
    @DisplayName("resolveFinalEvents")
    class FinalEvents {

        /**
         * SUMMARY:
         * Smoke test verifying that resolveFinalEvents can be called on a freshly constructed board without errors.
         *
         * EXPECTATION:
         * No exception is thrown when resolveFinalEvents is invoked on an initial 3-player board.
         */
        @Test
        @DisplayName("resolveFinalEvents does not throw on an initial board")
        void resolveFinalEventsDoesNotThrow() {
            List<Player> ps = players(3);
            Board b = new Board(3, ps);
            assertDoesNotThrow(() -> b.resolveFinalEvents(ps));
        }
    }
}

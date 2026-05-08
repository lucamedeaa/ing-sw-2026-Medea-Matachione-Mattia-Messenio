package it.polimi.ingsw.modelTest.boardTest;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.OfferTile;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.exceptions.InvalidGameActionException;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest extends ModelTest {

    // -----------------------------------------------------------------------
    // Helpers — build boards with different player counts
    // -----------------------------------------------------------------------

    private Board board(int n) {
        List<it.polimi.ingsw.model.Player> players = newPlayers(n);
        return new Board(n, players);
    }

    private List<it.polimi.ingsw.model.Player> players(int n) {
        return newPlayers(n);
    }

    // -----------------------------------------------------------------------
    // Construction
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Board construction")
    class Construction {

        @Test
        @DisplayName("2-player board initialises without exceptions")
        void twoPlayerBoardInitialises() {
            assertDoesNotThrow(() -> board(2));
        }

        @Test
        @DisplayName("3-player board initialises without exceptions")
        void threePlayerBoardInitialises() {
            assertDoesNotThrow(() -> board(3));
        }

        @Test
        @DisplayName("4-player board initialises without exceptions")
        void fourPlayerBoardInitialises() {
            assertDoesNotThrow(() -> board(4));
        }

        @Test
        @DisplayName("5-player board initialises without exceptions")
        void fivePlayerBoardInitialises() {
            assertDoesNotThrow(() -> board(5));
        }

        @Test
        @DisplayName("Invalid player count throws IllegalArgumentException")
        void invalidPlayerCountThrows() {
            List<Player> p = players(1);
            assertThrows(IllegalArgumentException.class, () -> new Board(1, p));
        }

        @Test
        @DisplayName("offerTrack is non-null and non-empty after construction")
        void offerTrackNonEmpty() {
            Board b = board(3);
            assertNotNull(b.getOfferTrack());
            assertFalse(b.getOfferTrack().isEmpty());
        }

        @Test
        @DisplayName("All offer tiles are free after construction")
        void allTilesFreeAtStart() {
            Board b = board(4);
            assertTrue(b.getOfferTrack().stream().allMatch(OfferTile::isFree));
        }
    }

    // -----------------------------------------------------------------------
    // Turn order
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Turn order (totem management)")
    class TurnOrder {

        @Test
        @DisplayName("getCurrentPlayer returns a non-null player at start")
        void getCurrentPlayerNonNull() {
            List<Player> ps = players(3);
            Board b = new Board(3, ps);
            assertNotNull(b.getCurrentPlayer());
        }

        @Test
        @DisplayName("getCurrentPlayer throws when all totems consumed")
        void getCurrentPlayerThrowsWhenEmpty() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            b.consumeCurrentPlayer();
            b.consumeCurrentPlayer();
            assertThrows(IllegalStateException.class, b::getCurrentPlayer);
        }

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

        @Test
        @DisplayName("allTotemsPlaced is false at start")
        void allTotemsPlacedFalseAtStart() {
            Board b = board(3);
            assertFalse(b.allTotemsPlaced());
        }

        @Test
        @DisplayName("allTotemsPlaced is true after consuming all players")
        void allTotemsPlacedTrueAfterAll() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            b.consumeCurrentPlayer();
            b.consumeCurrentPlayer();
            assertTrue(b.allTotemsPlaced());
        }

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

    // -----------------------------------------------------------------------
    // Offer track / totem placement
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Offer track & placeTotem")
    class OfferTrackTests {

        @Test
        @DisplayName("placeTotem occupies a free tile")
        void placeTotemOccupiesTile() {
            List<Player> ps = players(3);
            Board b = new Board(3, ps);
            Player p = ps.get(0);
            b.placeTotem(0, p);
            assertFalse(b.getOfferTrack().get(0).isFree());
        }

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

        @Test
        @DisplayName("placeTotem with negative index throws InvalidGameActionException")
        void placeTotemNegativeIndexThrows() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            assertThrows(InvalidGameActionException.class, () -> b.placeTotem(-1, ps.get(0)));
        }

        @Test
        @DisplayName("placeTotem with out-of-bounds index throws InvalidGameActionException")
        void placeTotemOutOfBoundsThrows() {
            List<Player> ps = players(2);
            Board b = new Board(2, ps);
            int outOfBounds = b.getOfferTrack().size();
            assertThrows(InvalidGameActionException.class, () -> b.placeTotem(outOfBounds, ps.get(0)));
        }
    }

    // -----------------------------------------------------------------------
    // Card access (peekCard / takeCard)
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Card access: peekCard & takeCard")
    class CardAccess {

        @Test
        @DisplayName("peekCard(0, 0) returns a non-null card from upper row")
        void peekUpperRowReturnsCard() {
            Board b = board(2);
            assertNotNull(b.peekCard(0, 0));
        }

        @Test
        @DisplayName("peekCard(1, 0) returns a non-null card from lower row")
        void peekLowerRowReturnsCard() {
            Board b = board(2);
            assertNotNull(b.peekCard(1, 0));
        }

        @Test
        @DisplayName("takeCard removes the card (second peek on same slot throws)")
        void takeCardRemovesCard() {
            Board b = board(2);
            b.takeCard(0, 0);
            assertThrows(InvalidGameActionException.class, () -> b.peekCard(0, 0));
        }

        @Test
        @DisplayName("takeCard returns the correct card (same object as peek)")
        void takeCardReturnsSameAspeek() {
            Board b = board(3);
            Card peeked = b.peekCard(0, 0);
            Card taken = b.takeCard(0, 0);
            assertSame(peeked, taken);
        }

        @Test
        @DisplayName("peekCard on already-taken slot throws InvalidGameActionException")
        void peekOnTakenSlotThrows() {
            Board b = board(2);
            b.takeCard(1, 0);
            assertThrows(InvalidGameActionException.class, () -> b.peekCard(1, 0));
        }
    }

    // -----------------------------------------------------------------------
    // Round cleanup
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("cleanupForNextRound")
    class RoundCleanup {

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

    // -----------------------------------------------------------------------
    // resolveFinalEvents (smoke test — must not throw)
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("resolveFinalEvents")
    class FinalEvents {

        @Test
        @DisplayName("resolveFinalEvents does not throw on an initial board")
        void resolveFinalEventsDoesNotThrow() {
            List<Player> ps = players(3);
            Board b = new Board(3, ps);
            assertDoesNotThrow(() -> b.resolveFinalEvents(ps));
        }
    }
}

package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.ModelObserver;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.update.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest extends ModelTest {

    private Game game(int n) {
        List<String> names = new java.util.ArrayList<>();
        for (int i = 1; i <= n; i++) names.add("Player" + i);
        return new Game(names);
    }

    @Nested
    @DisplayName("Construction")
    class Construction {

        /**
         * SUMMARY:
         * Verifies that creating a game with exactly 2 players (the minimum)
         * completes without throwing any exception.
         *
         * EXPECTATION:
         * Game construction succeeds for 2 players.
         */
        @Test
        @DisplayName("2-player game initialises without exceptions")
        void twoPlayerGameInitialises() {
            assertDoesNotThrow(() -> game(2));
        }

        /**
         * SUMMARY:
         * Verifies that creating a game with exactly 5 players (the maximum)
         * completes without throwing any exception.
         *
         * EXPECTATION:
         * Game construction succeeds for 5 players.
         */
        @Test
        @DisplayName("5-player game initialises without exceptions")
        void fivePlayerGameInitialises() {
            assertDoesNotThrow(() -> game(5));
        }

        /**
         * SUMMARY:
         * Verifies that attempting to create a game with only 1 player
         * is rejected as invalid.
         *
         * EXPECTATION:
         * IllegalArgumentException is thrown for 1 player.
         */
        @Test
        @DisplayName("1 player throws IllegalArgumentException")
        void onePlayerThrows() {
            assertThrows(IllegalArgumentException.class, () -> game(1));
        }

        /**
         * SUMMARY:
         * Verifies that attempting to create a game with 6 players
         * (exceeding the maximum) is rejected.
         *
         * EXPECTATION:
         * IllegalArgumentException is thrown for 6 players.
         */
        @Test
        @DisplayName("6 players throws IllegalArgumentException")
        void sixPlayersThrows() {
            assertThrows(IllegalArgumentException.class, () -> game(6));
        }

        /**
         * SUMMARY:
         * Verifies that passing null instead of a player list
         * is rejected by the constructor.
         *
         * EXPECTATION:
         * IllegalArgumentException is thrown for a null player list.
         */
        @Test
        @DisplayName("null players throws IllegalArgumentException")
        void nullPlayersThrows() {
            assertThrows(IllegalArgumentException.class, () -> new Game(null));
        }

        /**
         * SUMMARY:
         * Verifies that getPlayers() returns a list whose size matches
         * the number of players provided at construction.
         *
         * EXPECTATION:
         * getPlayers().size() equals 3 for a 3-player game.
         */
        @Test
        @DisplayName("getPlayers returns correct number of players")
        void getPlayersCorrectSize() {
            assertEquals(3, game(3).getPlayers().size());
        }

        /**
         * SUMMARY:
         * Verifies that the game board is properly initialised and
         * accessible after construction.
         *
         * EXPECTATION:
         * getBoard() returns a non-null object.
         */
        @Test
        @DisplayName("getBoard returns non-null board")
        void getBoardNonNull() {
            assertNotNull(game(2).getBoard());
        }

        /**
         * SUMMARY:
         * Verifies that the game starts at round 1 immediately
         * after construction.
         *
         * EXPECTATION:
         * getCurrentRound() returns 1 for a freshly created game.
         */
        @Test
        @DisplayName("getCurrentRound starts at 1")
        void initialRoundIsOne() {
            assertEquals(1, game(2).getCurrentRound());
        }
    }

    @Nested
    @DisplayName("Round management")
    class RoundManagement {

        /**
         * SUMMARY:
         * Verifies that calling incrementRound() once advances the
         * game from round 1 to round 2.
         *
         * EXPECTATION:
         * getCurrentRound() returns 2 after one increment.
         */
        @Test
        @DisplayName("incrementRound increases round by 1")
        void incrementRoundByOne() {
            Game g = game(2);
            g.incrementRound();
            assertEquals(2, g.getCurrentRound());
        }
    }

    @Nested
    @DisplayName("Player command lookup")
    class PlayerCommandLookup {

        /**
         * SUMMARY:
         * Verifies that issuing a game command with an unknown player
         * nickname triggers the appropriate exception.
         *
         * EXPECTATION:
         * InvalidGameActionException is thrown for an unrecognised nickname.
         */
        @Test
        @DisplayName("throws InvalidGameActionException for unknown nickname")
        void throwsForUnknownNickname() {
            Game g = game(2);
            assertThrows(InvalidGameActionException.class, () -> g.takeCard("Unknown", 0, 0));
        }
    }

    @Nested
    @DisplayName("Observer notifications")
    class ObserverNotifications {

        /**
         * SUMMARY:
         * Verifies that after registering an observer and requesting a
         * full sync, the observer's onFullSync callback is invoked.
         *
         * EXPECTATION:
         * The boolean flag is set to true, confirming the observer was notified.
         */
        @Test
        @DisplayName("addObserver + notifyFullSync calls onFullSync on observer")
        void fullSyncNotifiesObserver() {
            Game g = game(2);
            boolean[] called = {false};
            g.addObserver(new ModelObserver() {
                @Override public void onModelUpdate(ModelUpdate update) {}
                @Override public void onFullSync(BoardUpdate board, List<PlayerUpdate> players, String activePlayer, List<AvailableAction> actions, InitTurnOrderTileUpdate turnOrderTile) {
                    called[0] = true;
                }
            });
            g.notifyFullSync();
            assertTrue(called[0]);
        }

        /**
         * SUMMARY:
         * Verifies that the full-sync notification passes the correct
         * number of player updates matching the game's player count.
         *
         * EXPECTATION:
         * The players list received by onFullSync has size 3 for a 3-player game.
         */
        @Test
        @DisplayName("notifyFullSync passes correct number of players to observer")
        void fullSyncPassesCorrectPlayers() {
            Game g = game(3);
            int[] count = {0};
            g.addObserver(new ModelObserver() {
                @Override public void onModelUpdate(ModelUpdate update) {}
                @Override public void onFullSync(BoardUpdate board, List<PlayerUpdate> players, String activePlayer, List<AvailableAction> actions, InitTurnOrderTileUpdate turnOrderTile) {
                    count[0] = players.size();
                }
            });
            g.notifyFullSync();
            assertEquals(3, count[0]);
        }
    }


    @Nested
    @DisplayName("start")
    class Start {

        /**
         * SUMMARY:
         * Verifies that calling start() on a properly configured game
         * (with an observer registered) does not throw any exception.
         *
         * EXPECTATION:
         * start() completes without error.
         */
        @Test
        @DisplayName("start does not throw")
        void startDoesNotThrow() {
            Game g = game(2);
            g.addObserver(new ModelObserver() {
                @Override public void onModelUpdate(ModelUpdate update) {}
                @Override public void onFullSync(BoardUpdate board, List<PlayerUpdate> players, String activePlayer, List<AvailableAction> actions, InitTurnOrderTileUpdate turnOrderTile) {}
            });
            assertDoesNotThrow(g::start);
        }

        /**
         * SUMMARY:
         * Verifies that after starting the game, the internal state
         * machine is initialised and accessible.
         *
         * EXPECTATION:
         * getCurrentState() returns a non-null value after start().
         */
        @Test
        @DisplayName("after start, getCurrentState is non-null")
        void afterStartStateNonNull() {
            Game g = game(2);
            g.addObserver(new ModelObserver() {
                @Override public void onModelUpdate(ModelUpdate update) {}
                @Override public void onFullSync(BoardUpdate board, List<PlayerUpdate> players, String activePlayer, List<AvailableAction> actions,  InitTurnOrderTileUpdate turnOrderTile) {}
            });
            g.start();
            assertNotNull(g.getCurrentState());
        }
    }
}

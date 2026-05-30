package it.polimi.ingsw.modelTest.gameStateTest;

import it.polimi.ingsw.modelTest.ModelTest;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.state.GameEndedState;
import it.polimi.ingsw.server.model.state.PlacementState;
import it.polimi.ingsw.server.model.state.RoundEndState;
import it.polimi.ingsw.server.model.update.AvailableAction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoundEndStateTest extends ModelTest {

    private Game game(int n) {
        return new Game(playerNames(n));
    }

    private List<String> playerNames(int n) {
        return java.util.stream.IntStream.rangeClosed(1, n)
                .mapToObj(i -> "Player" + i)
                .toList();
    }

    private RoundEndState roundEndState(Game game) {
        return new RoundEndState(game);
    }

    @Nested
    @DisplayName("RoundEndState start")
    class StartTests {

        /**
         * SUMMARY:
         * Verifies that calling start() on RoundEndState increments the game's round counter by one.
         *
         * EXPECTATION:
         * The current round after start() should be one more than the round before.
         */
        @Test
        @DisplayName("start increments round")
        void startIncrementsRound() {
            Game game = game(2);
            RoundEndState state = roundEndState(game);

            int roundBefore = game.getCurrentRound();
            state.start();

            assertEquals(roundBefore + 1, game.getCurrentRound());
        }

        /**
         * SUMMARY:
         * Verifies that start() transitions the game to PlacementState when the game has not reached the final round.
         *
         * EXPECTATION:
         * The game's current state should be an instance of PlacementState.
         */
        @Test
        @DisplayName("start transitions to PlacementState when game is not over")
        void startTransitionsToPlacementStateWhenGameIsNotOver() {
            Game game = game(2);
            RoundEndState state = roundEndState(game);
            state.start();

            assertTrue(game.getCurrentState() instanceof PlacementState);
        }

        /**
         * SUMMARY:
         * Verifies that start() ends the game when it is already at round 10 (the final round),
         * transitioning to either ScoringState or GameEndedState.
         *
         * EXPECTATION:
         * The round should remain 10, and the current state should be ScoringState or GameEndedState.
         */
        @Test
        @DisplayName("start ends the game at the end of round 10")
        void startEndsGameWhenRoundIsTen() {
            Game game = game(2);

            for (int i = 0; i < 9; i++) {
                game.incrementRound();
            }

            assertEquals(10, game.getCurrentRound(), "Prima di RoundEndState, il round attuale deve essere 10");

            RoundEndState state = roundEndState(game);
            state.start();

            assertEquals(10, game.getCurrentRound());

            assertTrue(game.getCurrentState() instanceof it.polimi.ingsw.server.model.state.ScoringState ||
                    game.getCurrentState() instanceof GameEndedState);
        }
    }

    @Nested
    @DisplayName("Available actions")
    class AvailableActionsTests {

        /**
         * SUMMARY:
         * Verifies that getAvailableActions returns an empty list in RoundEndState,
         * since no player actions are possible during round transitions.
         *
         * EXPECTATION:
         * The returned list of available actions should be empty.
         */
        @Test
        @DisplayName("getAvailableActions returns empty list")
        void getAvailableActionsReturnsEmptyList() {
            Game game = game(2);
            RoundEndState state = roundEndState(game);
            List<AvailableAction> actions = state.getAvailableActions("Player1");

            assertTrue(actions.isEmpty());
        }
    }

    @Nested
    @DisplayName("Active player")
    class ActivePlayerTests {

        /**
         * SUMMARY:
         * Verifies that getActivePlayerNickname returns null in RoundEndState,
         * since no player is active during round transitions.
         *
         * EXPECTATION:
         * The active player nickname should be null.
         */
        @Test
        @DisplayName("getActivePlayerNickname returns null")
        void getActivePlayerNicknameReturnsNull() {
            Game game = game(2);
            RoundEndState state = roundEndState(game);

            assertNull(state.getActivePlayerNickname());
        }
    }
}
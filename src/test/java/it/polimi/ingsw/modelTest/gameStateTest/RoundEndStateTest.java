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

        @Test
        @DisplayName("start increments round")
        void startIncrementsRound() {
            Game game = game(2);
            RoundEndState state = roundEndState(game);

            int roundBefore = game.getCurrentRound();
            state.start();

            assertEquals(roundBefore + 1, game.getCurrentRound());
        }

        @Test
        @DisplayName("start transitions to PlacementState when game is not over")
        void startTransitionsToPlacementStateWhenGameIsNotOver() {
            Game game = game(2);
            RoundEndState state = roundEndState(game);
            state.start();

            assertTrue(game.getCurrentState() instanceof PlacementState);
        }

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

        @Test
        @DisplayName("getActivePlayerNickname returns null")
        void getActivePlayerNicknameReturnsNull() {
            Game game = game(2);
            RoundEndState state = roundEndState(game);

            assertNull(state.getActivePlayerNickname());
        }
    }
}
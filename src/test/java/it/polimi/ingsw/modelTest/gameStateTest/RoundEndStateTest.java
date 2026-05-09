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
        @DisplayName("start ends the game when round becomes greater than 10")
        void startEndsGameWhenRoundBecomesGreaterThanTen() {
            Game game = game(2);

            for (int i = 0; i < 9; i++) {
                game.incrementRound();
            }

            RoundEndState state = roundEndState(game);

            state.start();

            assertEquals(11, game.getCurrentRound());
            assertTrue(game.getCurrentState() instanceof GameEndedState);
            assertTrue(game.getCurrentState().isEnded());
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
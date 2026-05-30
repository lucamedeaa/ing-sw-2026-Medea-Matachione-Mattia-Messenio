package it.polimi.ingsw.modelTest.gameStateTest;

import it.polimi.ingsw.modelTest.ModelTest;
import it.polimi.ingsw.server.model.CompletedGameResult;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.PlayerGameResult;
import it.polimi.ingsw.server.model.state.GameEndedState;
import it.polimi.ingsw.server.model.state.ScoringState;
import it.polimi.ingsw.server.model.update.AvailableAction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ScoringStateTest extends ModelTest {

    private Game game(int n) {
        return new Game(playerNames(n));
    }

    private List<String> playerNames(int n) {
        return java.util.stream.IntStream.rangeClosed(1, n)
                .mapToObj(i -> "Player" + i)
                .toList();
    }

    private CompletedGameResult runScoring(Game game) {
        AtomicReference<CompletedGameResult> result = new AtomicReference<>();
        game.setCompletionHandler(result::set);
        game.changeState(new ScoringState(game));
        return result.get();
    }

    private void emptyFood(Player player) {
        player.addFood(-player.getFood());
    }

    private void setFood(Player player, int amount) {
        emptyFood(player);
        player.addFood(amount);
    }

    @Nested
    @DisplayName("ScoringState start")
    class StartTests {

        /**
         * SUMMARY:
         * Verifies that running the scoring phase transitions the game to GameEndedState
         * and that the game is flagged as ended.
         *
         * EXPECTATION:
         * The current state should be GameEndedState and isEnded() should return true.
         */
        @Test
        @DisplayName("start ends the game")
        void startEndsGame() {
            Game game = game(2);

            runScoring(game);

            assertTrue(game.getCurrentState() instanceof GameEndedState);
            assertTrue(game.getCurrentState().isEnded());
        }

        /**
         * SUMMARY:
         * Verifies that the scoring phase produces exactly one PlayerGameResult per player.
         *
         * EXPECTATION:
         * The result should be non-null and contain 3 player results for a 3-player game.
         */
        @Test
        @DisplayName("start produces one result per player")
        void startProducesOneResultPerPlayer() {
            Game game = game(3);

            CompletedGameResult result = runScoring(game);

            assertNotNull(result);
            assertEquals(3, result.playerResults().size());
        }

        /**
         * SUMMARY:
         * Verifies that the leaderboard is sorted by final score in descending order
         * when players have different prestige values.
         *
         * EXPECTATION:
         * The player with the highest prestige should be first, followed by the others in descending order.
         */
        @Test
        @DisplayName("leaderboard is ordered by final score descending")
        void leaderboardOrderedByFinalScoreDescending() {
            Game game = game(3);
            List<Player> players = game.getPlayers();

            players.get(0).addPrestige(5);
            players.get(1).addPrestige(20);
            players.get(2).addPrestige(10);

            CompletedGameResult result = runScoring(game);
            List<PlayerGameResult> leaderboard = result.playerResults();

            assertEquals(players.get(1).getNickname(), leaderboard.get(0).nickname());
            assertEquals(players.get(2).getNickname(), leaderboard.get(1).nickname());
            assertEquals(players.get(0).getNickname(), leaderboard.get(2).nickname());
        }

        /**
         * SUMMARY:
         * Verifies that remaining food is used as a tiebreaker when two players
         * have the same final score.
         *
         * EXPECTATION:
         * The player with more remaining food should be ranked higher.
         */
        @Test
        @DisplayName("remaining food breaks ties on final score")
        void remainingFoodBreaksScoreTies() {
            Game game = game(2);
            List<Player> players = game.getPlayers();

            players.get(0).addPrestige(10);
            players.get(1).addPrestige(10);

            setFood(players.get(0), 1);
            setFood(players.get(1), 5);

            CompletedGameResult result = runScoring(game);
            List<PlayerGameResult> leaderboard = result.playerResults();

            assertEquals(players.get(1).getNickname(), leaderboard.get(0).nickname());
            assertEquals(players.get(0).getNickname(), leaderboard.get(1).nickname());
        }

        /**
         * SUMMARY:
         * Verifies that two players with identical scores and identical food
         * share the same leaderboard position.
         *
         * EXPECTATION:
         * Both players should have position 1.
         */
        @Test
        @DisplayName("players with same score and food share the same position")
        void sameScoreAndFoodShareSamePosition() {
            Game game = game(2);
            List<Player> players = game.getPlayers();

            players.get(0).addPrestige(10);
            players.get(1).addPrestige(10);

            setFood(players.get(0), 3);
            setFood(players.get(1), 3);

            CompletedGameResult result = runScoring(game);
            List<PlayerGameResult> leaderboard = result.playerResults();

            assertEquals(1, leaderboard.get(0).position());
            assertEquals(1, leaderboard.get(1).position());
        }
    }

    @Nested
    @DisplayName("Available actions")
    class AvailableActionsTests {

        /**
         * SUMMARY:
         * Verifies that getAvailableActions returns an empty list in ScoringState,
         * since no player actions are possible during final scoring.
         *
         * EXPECTATION:
         * The returned list of available actions should be empty.
         */
        @Test
        @DisplayName("getAvailableActions returns empty list")
        void getAvailableActionsReturnsEmptyList() {
            ScoringState state = new ScoringState(game(2));

            List<AvailableAction> actions = state.getAvailableActions("Player1");

            assertTrue(actions.isEmpty());
        }
    }

    @Nested
    @DisplayName("Active player")
    class ActivePlayerTests {

        /**
         * SUMMARY:
         * Verifies that getActivePlayerNickname returns null in ScoringState,
         * since no player is active during scoring.
         *
         * EXPECTATION:
         * The active player nickname should be null.
         */
        @Test
        @DisplayName("getActivePlayerNickname returns null")
        void getActivePlayerNicknameReturnsNull() {
            ScoringState state = new ScoringState(game(2));

            assertNull(state.getActivePlayerNickname());
        }
    }

    @Nested
    @DisplayName("Three-way tie")
    class ThreeWayTieTests {

        /**
         * SUMMARY:
         * Verifies that when 3 players finish with identical scores and identical food,
         * all 3 share position 1 on the leaderboard.
         *
         * EXPECTATION:
         * All three PlayerGameResult entries should have position 1.
         */
        @Test
        @DisplayName("three players with same score and food all share position 1")
        void threePlayersWithSameScoreAndFoodSharePosition() {
            Game game = game(3);
            List<Player> players = game.getPlayers();

            players.get(0).addPrestige(15);
            players.get(1).addPrestige(15);
            players.get(2).addPrestige(15);

            setFood(players.get(0), 4);
            setFood(players.get(1), 4);
            setFood(players.get(2), 4);

            CompletedGameResult result = runScoring(game);
            List<PlayerGameResult> leaderboard = result.playerResults();

            assertEquals(3, leaderboard.size());
            assertEquals(1, leaderboard.get(0).position());
            assertEquals(1, leaderboard.get(1).position());
            assertEquals(1, leaderboard.get(2).position());
        }
    }
}
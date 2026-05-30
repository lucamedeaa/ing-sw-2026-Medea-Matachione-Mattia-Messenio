package it.polimi.ingsw.modelTest.gameStateTest;

import it.polimi.ingsw.modelTest.ModelTest;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.ModelObserver;
import it.polimi.ingsw.server.model.state.InitState;
import it.polimi.ingsw.server.model.state.PlacementState;
import it.polimi.ingsw.server.model.update.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InitStateTest extends ModelTest {

    private Game game(int n) {
        return new Game(playerNames(n));
    }

    private List<String> playerNames(int n) {
        return java.util.stream.IntStream.rangeClosed(1, n)
                .mapToObj(i -> "Player" + i)
                .toList();
    }

    private InitState initState(Game game) {
        return new InitState(game);
    }

    @Nested
    @DisplayName("InitState start")
    class StartTests {

        /**
         * SUMMARY:
         * Verifies that calling start() on InitState transitions the game to PlacementState.
         *
         * EXPECTATION:
         * The game's current state should be an instance of PlacementState after start() is invoked.
         */
        @Test
        @DisplayName("start transitions to PlacementState")
        void startTransitionsToPlacementState() {
            Game game = game(2);
            InitState state = initState(game);

            state.start();

            assertTrue(game.getCurrentState() instanceof PlacementState);
        }

        /**
         * SUMMARY:
         * Verifies that start() sends a full synchronization to all registered observers,
         * including board and player updates.
         *
         * EXPECTATION:
         * The observer should receive a full sync with a non-null BoardUpdate and
         * a PlayerUpdate list matching the number of players (2).
         */
        @Test
        @DisplayName("start sends a full sync to observers")
        void startSendsFullSync() {
            Game game = game(2);
            InitState state = initState(game);
            TestObserver observer = new TestObserver();

            game.addObserver(observer);

            state.start();

            assertTrue(observer.fullSyncReceived);
            assertNotNull(observer.boardUpdate);
            assertNotNull(observer.playersUpdates);
            assertEquals(2, observer.playersUpdates.size());
        }
    }

    @Nested
    @DisplayName("Available actions")
    class AvailableActionsTests {

        /**
         * SUMMARY:
         * Verifies that getAvailableActions returns an empty list during the InitState,
         * since no player actions are allowed before the game starts.
         *
         * EXPECTATION:
         * The returned list of available actions should be empty.
         */
        @Test
        @DisplayName("getAvailableActions returns empty list")
        void getAvailableActionsReturnsEmptyList() {
            Game game = game(2);
            InitState state = initState(game);

            assertTrue(state.getAvailableActions("Player1").isEmpty());
        }
    }

    @Nested
    @DisplayName("Active player")
    class ActivePlayerTests {

        /**
         * SUMMARY:
         * Verifies that getActivePlayerNickname returns null in InitState,
         * since no player is active before the game begins.
         *
         * EXPECTATION:
         * The active player nickname should be null.
         */
        @Test
        @DisplayName("getActivePlayerNickname returns null")
        void getActivePlayerNicknameReturnsNull() {
            Game game = game(2);
            InitState state = initState(game);

            assertNull(state.getActivePlayerNickname());
        }
    }

    private static class TestObserver implements ModelObserver {
        private boolean fullSyncReceived = false;
        private BoardUpdate boardUpdate;
        private List<PlayerUpdate> playersUpdates;

        @Override
        public void onModelUpdate(ModelUpdate update) {
            // Not needed for InitState tests
        }

        @Override
        public void onFullSync(BoardUpdate board,
                               List<PlayerUpdate> players,
                               String activePlayer,
                               List<AvailableAction> actions,
                               InitTurnOrderTileUpdate turnOrderTile) {
            this.fullSyncReceived = true;
            this.boardUpdate = board;
            this.playersUpdates = players;
        }
    }
}
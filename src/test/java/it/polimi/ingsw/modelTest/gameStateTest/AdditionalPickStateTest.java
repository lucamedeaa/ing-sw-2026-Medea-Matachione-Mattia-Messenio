package it.polimi.ingsw.modelTest.gameStateTest;

import it.polimi.ingsw.modelTest.ModelTest;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.card.building.LatePurchase;
import it.polimi.ingsw.server.model.card.event.Hunt;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.state.AdditionalPickState;
import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.AvailableAction.SkipAction;
import it.polimi.ingsw.server.model.update.AvailableAction.TakeCardAction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AdditionalPickStateTest extends ModelTest {

    private Game game(int n) {
        return new Game(playerNames(n));
    }

    private List<String> playerNames(int n) {
        return java.util.stream.IntStream.rangeClosed(1, n)
                .mapToObj(i -> "Player" + i)
                .toList();
    }

    private AdditionalPickState additionalPickState(Game game) {
        return new AdditionalPickState(game);
    }

    private int firstPickableCardIndex(Board board, int rowIdx) {
        List<Optional<Card>> row = board.getRow(rowIdx);

        for (int i = 0; i < row.size(); i++) {
            if (row.get(i).isPresent() && row.get(i).get().isPickable()) {
                return i;
            }
        }

        return -1;
    }

    private int firstPickableBuildingIndex(Board board, int rowIdx) {
        List<Optional<Card>> row = board.getRow(rowIdx);

        for (int i = 0; i < row.size(); i++) {
            if (row.get(i).isPresent()) {
                Card card = row.get(i).get();

                if (card.isPickable() && card.isPersistent()) {
                    return i;
                }
            }
        }

        return -1;
    }

    private void giveBonusPick(Player player) {
        player.addCard(new LatePurchase(110));
    }

    private void giveFood(Player player, int amount) {
        player.addFood(amount);
    }

    private void emptyFood(Player player) {
        player.addFood(-player.getFood());
    }

    @Nested
    @DisplayName("AdditionalPickState start")
    class StartTests {

        /**
         * SUMMARY:
         * Verifies that when no player has bonus picks, start() transitions
         * the game out of AdditionalPickState (to RoundEndState).
         *
         * EXPECTATION:
         * The current state should no longer be AdditionalPickState.
         */
        @Test
        @DisplayName("when no player has bonus picks, state transitions to RoundEndState")
        void transitionsToRoundEndStateWhenNoBonusPicks() {
            Game game = game(2);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertFalse(game.getCurrentState() instanceof AdditionalPickState);
        }

        /**
         * SUMMARY:
         * Verifies that start() selects the first player who has bonus picks as the active player.
         *
         * EXPECTATION:
         * The active player nickname should match the player who was given a bonus pick.
         */
        @Test
        @DisplayName("start selects first player with bonus picks")
        void startSelectsFirstPlayerWithBonusPicks() {
            Game game = game(2);
            Player player = game.getPlayers().get(0);

            giveBonusPick(player);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertEquals(player.getNickname(), state.getActivePlayerNickname());
        }

        /**
         * SUMMARY:
         * Verifies that start() skips players without bonus picks and selects the first eligible one.
         *
         * EXPECTATION:
         * The active player should be the second player (who has a bonus pick), not the first.
         */
        @Test
        @DisplayName("start skips players without bonus picks")
        void startSkipsPlayersWithoutBonusPicks() {
            Game game = game(2);

            Player first = game.getPlayers().get(0);
            Player second = game.getPlayers().get(1);

            giveBonusPick(second);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertNotEquals(first.getNickname(), state.getActivePlayerNickname());
            assertEquals(second.getNickname(), state.getActivePlayerNickname());
        }
    }

    @Nested
    @DisplayName("Available actions")
    class AvailableActionsTests {

        /**
         * SUMMARY:
         * Verifies that the active player has both TakeCardAction and SkipAction available
         * during the additional pick phase.
         *
         * EXPECTATION:
         * The actions list should contain at least one TakeCardAction and one SkipAction.
         */
        @Test
        @DisplayName("active player has TakeCardAction and SkipAction")
        void activePlayerHasTakeAndSkipActions() {
            Game game = game(2);
            Player player = game.getPlayers().get(0);

            giveBonusPick(player);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            List<AvailableAction> actions = state.getAvailableActions(player.getNickname());

            assertTrue(actions.stream().anyMatch(a -> a instanceof TakeCardAction));
            assertTrue(actions.stream().anyMatch(a -> a instanceof SkipAction));
        }

        /**
         * SUMMARY:
         * Verifies that an inactive player has no available actions during the additional pick phase.
         *
         * EXPECTATION:
         * The available actions list for the inactive player should be empty.
         */
        @Test
        @DisplayName("inactive player has no available actions")
        void inactivePlayerHasNoActions() {
            Game game = game(2);

            Player active = game.getPlayers().get(0);
            Player inactive = game.getPlayers().get(1);

            giveBonusPick(active);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertTrue(state.getAvailableActions(inactive.getNickname()).isEmpty());
        }
    }

    @Nested
    @DisplayName("takeCard validation")
    class TakeCardValidation {

        /**
         * SUMMARY:
         * Verifies that a non-active player cannot take a bonus card, receiving an exception.
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown.
         */
        @Test
        @DisplayName("wrong player cannot take bonus card")
        void wrongPlayerCannotTakeBonusCard() {
            Game game = game(2);
            Board board = game.getBoard();

            Player active = game.getPlayers().get(0);
            Player inactive = game.getPlayers().get(1);

            giveBonusPick(active);

            int cardIdx = firstPickableCardIndex(board, 0);
            assertTrue(cardIdx >= 0);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.takeCard(inactive, 0, cardIdx));
        }

        /**
         * SUMMARY:
         * Verifies that the bonus pick can only take cards from the upper row (row 0),
         * not from the lower row (row 1).
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown when taking from row 1.
         */
        @Test
        @DisplayName("bonus pick can only take from upper row")
        void cannotTakeFromLowerRow() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            giveBonusPick(player);

            int cardIdx = firstPickableCardIndex(board, 1);
            assertTrue(cardIdx >= 0);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.takeCard(player, 1, cardIdx));
        }

        /**
         * SUMMARY:
         * Verifies that an event card cannot be taken with a bonus pick.
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown.
         */
        @Test
        @DisplayName("cannot take event card with bonus pick")
        void cannotTakeEventCard() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            giveBonusPick(player);

            board.addTopRow(new Hunt(56));
            int eventIdx = board.getRow(0).size() - 1;

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.takeCard(player, 0, eventIdx));
        }

        /**
         * SUMMARY:
         * Verifies that a building cannot be taken with a bonus pick when the player
         * has insufficient food.
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown.
         */
        @Test
        @DisplayName("cannot take building without enough food")
        void cannotTakeUnaffordableBuilding() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            giveBonusPick(player);
            emptyFood(player);

            int buildingIdx = firstPickableBuildingIndex(board, 0);
            assertTrue(buildingIdx >= 0);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.takeCard(player, 0, buildingIdx));
        }
    }

    @Nested
    @DisplayName("takeCard effects")
    class TakeCardEffects {

        /**
         * SUMMARY:
         * Verifies that taking a bonus card removes it from the board.
         *
         * EXPECTATION:
         * Attempting to peek the taken card's position should throw InvalidGameActionException.
         */
        @Test
        @DisplayName("taking a bonus card removes it from board")
        void takeCardRemovesCardFromBoard() {
            Game game = game(2);
            Board board = game.getBoard();

            Player first = game.getPlayers().get(0);
            Player second = game.getPlayers().get(1);

            giveBonusPick(first);
            giveBonusPick(second);
            giveFood(first, 20);

            int cardIdx = firstPickableCardIndex(board, 0);
            assertTrue(cardIdx >= 0);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            state.takeCard(first, 0, cardIdx);

            assertThrows(InvalidGameActionException.class,
                    () -> board.peekCard(0, cardIdx));
        }

        /**
         * SUMMARY:
         * Verifies that taking a building with a bonus pick deducts its final cost from the player's food.
         *
         * EXPECTATION:
         * The player's food should decrease by the building's final cost (after discount).
         */
        @Test
        @DisplayName("taking a building pays its final cost")
        void takeBuildingPaysFinalCost() {
            Game game = game(2);
            Board board = game.getBoard();

            Player first = game.getPlayers().get(0);
            Player second = game.getPlayers().get(1);

            giveBonusPick(first);
            giveBonusPick(second);
            giveFood(first, 20);

            int buildingIdx = firstPickableBuildingIndex(board, 0);
            assertTrue(buildingIdx >= 0);

            Card building = board.peekCard(0, buildingIdx);
            int foodBefore = first.getFood();
            int finalCost = Math.max(building.getFoodCost() - first.getFoodDiscount(), 0);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            state.takeCard(first, 0, buildingIdx);

            assertEquals(foodBefore - finalCost, first.getFood());
        }

        /**
         * SUMMARY:
         * Verifies that after using a bonus pick, the turn advances to the next eligible player.
         *
         * EXPECTATION:
         * The active player should become the second player.
         */
        @Test
        @DisplayName("after using bonus pick, turn advances to next eligible player")
        void takeCardAdvancesToNextEligiblePlayer() {
            Game game = game(2);
            Board board = game.getBoard();

            Player first = game.getPlayers().get(0);
            Player second = game.getPlayers().get(1);

            giveBonusPick(first);
            giveBonusPick(second);
            giveFood(first, 20);

            int cardIdx = firstPickableCardIndex(board, 0);
            assertTrue(cardIdx >= 0);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            state.takeCard(first, 0, cardIdx);

            assertEquals(second.getNickname(), state.getActivePlayerNickname());
        }
    }

    @Nested
    @DisplayName("skipBonus")
    class SkipBonusTests {

        /**
         * SUMMARY:
         * Verifies that a non-active player cannot skip the bonus pick phase.
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown.
         */
        @Test
        @DisplayName("wrong player cannot skip bonus")
        void wrongPlayerCannotSkipBonus() {
            Game game = game(2);

            Player active = game.getPlayers().get(0);
            Player inactive = game.getPlayers().get(1);

            giveBonusPick(active);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.skipBonus(inactive));
        }

        /**
         * SUMMARY:
         * Verifies that calling skipBonus advances the turn to the next eligible player.
         *
         * EXPECTATION:
         * The active player should become the second player after the first player skips.
         */
        @Test
        @DisplayName("skipBonus advances to next eligible player")
        void skipBonusAdvancesToNextEligiblePlayer() {
            Game game = game(2);

            Player first = game.getPlayers().get(0);
            Player second = game.getPlayers().get(1);

            giveBonusPick(first);
            giveBonusPick(second);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            state.skipBonus(first);

            assertEquals(second.getNickname(), state.getActivePlayerNickname());
        }
    }
}
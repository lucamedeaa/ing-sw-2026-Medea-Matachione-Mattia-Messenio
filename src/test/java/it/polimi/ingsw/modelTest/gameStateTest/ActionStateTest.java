package it.polimi.ingsw.modelTest.gameStateTest;

import it.polimi.ingsw.modelTest.ModelTest;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.card.Card;
import it.polimi.ingsw.server.model.card.event.Hunt;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.state.ActionState;
import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.AvailableAction.SkipAction;
import it.polimi.ingsw.server.model.update.AvailableAction.TakeCardAction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ActionStateTest extends ModelTest {

    private Game game(int n) {
        return new Game(playerNames(n));
    }

    private List<String> playerNames(int n) {
        return java.util.stream.IntStream.rangeClosed(1, n)
                .mapToObj(i -> "Player" + i)
                .toList();
    }

    private ActionState actionState(Game game) {
        return new ActionState(game);
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

    private void removeAllPickableCharacters(Board board) {
        for (int rowIdx = 0; rowIdx <= 1; rowIdx++) {
            List<Optional<Card>> row = board.getRow(rowIdx);

            for (int i = 0; i < row.size(); i++) {
                if (row.get(i).isPresent()) {
                    Card card = row.get(i).get();

                    if (card.isPickable() && !card.isPersistent()) {
                        board.takeCard(rowIdx, i);
                    }
                }
            }
        }
    }

    private void emptyFood(Player player) {
        player.addFood(-player.getFood());
    }

    private void giveFood(Player player, int amount) {
        player.addFood(amount);
    }

    @Nested
    @DisplayName("ActionState start")
    class StartTests {

        /**
         * SUMMARY:
         * Verifies that the active player is null before start() is called on ActionState.
         *
         * EXPECTATION:
         * getActivePlayerNickname() should return null.
         */
        @Test
        @DisplayName("active player is null before start")
        void activePlayerNullBeforeStart() {
            Game game = game(2);
            ActionState state = actionState(game);

            assertNull(state.getActivePlayerNickname());
        }

        /**
         * SUMMARY:
         * Verifies that start() selects the player whose totem is on the first occupied offer tile.
         *
         * EXPECTATION:
         * The active player nickname should match the player placed on that tile.
         */
        @Test
        @DisplayName("start selects player on first occupied offer tile")
        void startSelectsFirstOccupiedTile() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            board.placeTotem(1, player);

            ActionState state = actionState(game);
            state.start();

            assertEquals(player.getNickname(), state.getActivePlayerNickname());
        }

        /**
         * SUMMARY:
         * Verifies that when a player is placed on offer tile 0, the food bonus from that tile
         * is applied exactly once upon start().
         *
         * EXPECTATION:
         * The player's food should increase by the tile's bonus (6) after start().
         */
        @Test
        @DisplayName("offer tile food bonus is applied once")
        void offerTileFoodBonusAppliedOnce() {
            Game game = game(5);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            board.placeTotem(0, player);
            int foodBefore = player.getFood();

            ActionState state = actionState(game);
            state.start();

            assertEquals(foodBefore + 6, player.getFood());
        }
    }

    @Nested
    @DisplayName("Available actions")
    class AvailableActionsTests {

        /**
         * SUMMARY:
         * Verifies that an inactive player has no available actions during the action phase.
         *
         * EXPECTATION:
         * The available actions list for the inactive player should be empty.
         */
        @Test
        @DisplayName("inactive player has no available actions")
        void inactivePlayerHasNoActions() {
            Game game = game(2);
            Board board = game.getBoard();

            Player active = game.getPlayers().get(0);
            Player inactive = game.getPlayers().get(1);

            board.placeTotem(1, active);

            ActionState state = actionState(game);
            state.start();

            assertTrue(state.getAvailableActions(inactive.getNickname()).isEmpty());
        }

        /**
         * SUMMARY:
         * Verifies that the active player receives a TakeCardAction among the available actions.
         *
         * EXPECTATION:
         * At least one action in the list should be an instance of TakeCardAction.
         */
        @Test
        @DisplayName("active player has TakeCardAction")
        void activePlayerHasTakeCardAction() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            board.placeTotem(1, player);

            ActionState state = actionState(game);
            state.start();

            List<AvailableAction> actions = state.getAvailableActions(player.getNickname());

            assertTrue(actions.stream().anyMatch(a -> a instanceof TakeCardAction));
        }

        /**
         * SUMMARY:
         * Verifies that a SkipAction is available when no pickable characters remain on the board.
         *
         * EXPECTATION:
         * At least one action in the list should be an instance of SkipAction.
         */
        @Test
        @DisplayName("SkipAction is available when no character can be picked")
        void skipAvailableWhenNoCharacterCanBePicked() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            removeAllPickableCharacters(board);
            giveFood(player, 20);
            board.placeTotem(1, player);

            ActionState state = actionState(game);
            state.start();

            List<AvailableAction> actions = state.getAvailableActions(player.getNickname());

            assertTrue(actions.stream().anyMatch(a -> a instanceof SkipAction));
        }
    }

    @Nested
    @DisplayName("takeCard validation")
    class TakeCardValidation {

        /**
         * SUMMARY:
         * Verifies that a non-active player cannot take a card, receiving an exception.
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown.
         */
        @Test
        @DisplayName("wrong player cannot take card")
        void wrongPlayerCannotTakeCard() {
            Game game = game(2);
            Board board = game.getBoard();

            Player active = game.getPlayers().get(0);
            Player inactive = game.getPlayers().get(1);

            int cardIdx = firstPickableCardIndex(board, 0);
            assertTrue(cardIdx >= 0);

            board.placeTotem(1, active);

            ActionState state = actionState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.takeCard(inactive, 0, cardIdx));
        }

        /**
         * SUMMARY:
         * Verifies that a player cannot take a card from a row where they have no remaining picks.
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown.
         */
        @Test
        @DisplayName("cannot take from row without remaining picks")
        void cannotTakeFromRowWithoutPicks() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            int cardIdx = firstPickableCardIndex(board, 1);
            assertTrue(cardIdx >= 0);

            board.placeTotem(1, player);

            ActionState state = actionState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.takeCard(player, 1, cardIdx));
        }

        /**
         * SUMMARY:
         * Verifies that an event card (e.g., Hunt) cannot be taken by a player.
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown.
         */
        @Test
        @DisplayName("cannot take event card")
        void cannotTakeEventCard() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            board.addTopRow(new Hunt(56));
            int eventIdx = board.getRow(0).size() - 1;

            board.placeTotem(1, player);

            ActionState state = actionState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.takeCard(player, 0, eventIdx));
        }

        /**
         * SUMMARY:
         * Verifies that a player cannot take a building card when they have insufficient food.
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

            emptyFood(player);

            int buildingIdx = firstPickableBuildingIndex(board, 0);
            assertTrue(buildingIdx >= 0);

            board.placeTotem(1, player);

            ActionState state = actionState(game);
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
         * Verifies that taking a character card adds it to the player's tribe.
         *
         * EXPECTATION:
         * The tribe size should increase by 1 and the tribe should contain the taken card.
         */
        @Test
        @DisplayName("taking a character adds it to the player's tribe")
        void takeCharacterAddsItToTribe() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            int cardIdx = firstPickableCardIndex(board, 0);
            assertTrue(cardIdx >= 0);
            Card character = board.peekCard(0, cardIdx);
            assertFalse(character.isPersistent());

            board.placeTotem(1, player);

            ActionState state = actionState(game);
            state.start();

            int tribeSizeBefore = player.getTribe().size();
            state.takeCard(player, 0, cardIdx);

            assertEquals(tribeSizeBefore + 1, player.getTribe().size());
            assertTrue(player.getTribe().contains(character));
        }

        /**
         * SUMMARY:
         * Verifies that taking a card removes it from the board so it cannot be peeked again.
         *
         * EXPECTATION:
         * Attempting to peek the taken card's position should throw InvalidGameActionException.
         */
        @Test
        @DisplayName("taking a card removes it from board")
        void takeCardRemovesCardFromBoard() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            int cardIdx = firstPickableCardIndex(board, 0);
            assertTrue(cardIdx >= 0);

            board.placeTotem(3, player);

            ActionState state = actionState(game);
            state.start();

            state.takeCard(player, 0, cardIdx);

            assertThrows(InvalidGameActionException.class,
                    () -> board.peekCard(0, cardIdx));
        }

        /**
         * SUMMARY:
         * Verifies that taking a building card deducts its final cost (after discount) from the player's food.
         *
         * EXPECTATION:
         * The player's food should decrease by the building's final cost.
         */
        @Test
        @DisplayName("taking a building pays its final cost")
        void takeCardPaysFinalCost() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            giveFood(player, 20);

            int buildingIdx = firstPickableBuildingIndex(board, 0);
            assertTrue(buildingIdx >= 0);

            Card building = board.peekCard(0, buildingIdx);
            int foodBefore = player.getFood();
            int finalCost = Math.max(building.getFoodCost() - player.getFoodDiscount(), 0);

            board.placeTotem(3, player);

            ActionState state = actionState(game);
            state.start();

            state.takeCard(player, 0, buildingIdx);

            assertEquals(foodBefore - finalCost, player.getFood());
        }

        /**
         * SUMMARY:
         * Verifies that the turn automatically advances to the next player when the current
         * player has exhausted all their picks.
         *
         * EXPECTATION:
         * The active player should be the second player after the first player takes a card.
         */
        @Test
        @DisplayName("turn advances automatically when picks are exhausted")
        void turnAdvancesWhenPicksAreExhausted() {
            Game game = game(2);
            Board board = game.getBoard();

            Player first = game.getPlayers().get(0);
            Player second = game.getPlayers().get(1);

            int cardIdx = firstPickableCardIndex(board, 0);
            assertTrue(cardIdx >= 0);

            board.placeTotem(1, first);
            board.placeTotem(2, second);

            ActionState state = actionState(game);
            state.start();

            state.takeCard(first, 0, cardIdx);

            assertEquals(second.getNickname(), state.getActivePlayerNickname());
        }
    }

    @Nested
    @DisplayName("endPlayerTurn")
    class EndPlayerTurnTests {

        /**
         * SUMMARY:
         * Verifies that endPlayerTurn clears the current tile and advances to the next player.
         *
         * EXPECTATION:
         * The first player's tile should be free and the active player should be the second player.
         */
        @Test
        @DisplayName("endPlayerTurn clears current tile and advances")
        void endPlayerTurnClearsTileAndAdvances() {
            Game game = game(2);
            Board board = game.getBoard();

            Player first = game.getPlayers().get(0);
            Player second = game.getPlayers().get(1);

            board.placeTotem(1, first);
            board.placeTotem(2, second);

            ActionState state = actionState(game);
            state.start();

            state.endPlayerTurn();

            assertTrue(board.getOfferTrack().get(1).isFree());
            assertEquals(second.getNickname(), state.getActivePlayerNickname());
        }

        /**
         * SUMMARY:
         * Verifies that endPlayerTurn transitions out of ActionState when no occupied tiles remain.
         *
         * EXPECTATION:
         * The game's current state should no longer be ActionState.
         */
        @Test
        @DisplayName("endPlayerTurn leaves ActionState when no occupied tiles remain")
        void endPlayerTurnLeavesActionStateWhenNoPlayersRemain() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            board.placeTotem(1, player);

            ActionState state = actionState(game);
            state.start();

            state.endPlayerTurn();

            assertFalse(game.getCurrentState() instanceof ActionState);
        }
    }

    @Nested
    @DisplayName("skipBonus")
    class SkipBonusTests {

        /**
         * SUMMARY:
         * Verifies that a non-active player cannot skip their bonus.
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown.
         */
        @Test
        @DisplayName("wrong player cannot skip")
        void wrongPlayerCannotSkip() {
            Game game = game(2);
            Board board = game.getBoard();

            Player active = game.getPlayers().get(0);
            Player inactive = game.getPlayers().get(1);

            removeAllPickableCharacters(board);
            board.placeTotem(1, active);

            ActionState state = actionState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.skipBonus(inactive));
        }

        /**
         * SUMMARY:
         * Verifies that a player cannot skip when there are still pickable characters on the board.
         *
         * EXPECTATION:
         * An InvalidGameActionException should be thrown.
         */
        @Test
        @DisplayName("cannot skip while a character can still be picked")
        void cannotSkipWithCharacterAvailable() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            assertTrue(firstPickableCardIndex(board, 0) >= 0 || firstPickableCardIndex(board, 1) >= 0);

            board.placeTotem(1, player);

            ActionState state = actionState(game);
            state.start();

            assertThrows(InvalidGameActionException.class,
                    () -> state.skipBonus(player));
        }

        /**
         * SUMMARY:
         * Verifies that calling skipBonus ends the current player's turn and advances to the next player.
         *
         * EXPECTATION:
         * The active player should become the second player after the first player skips.
         */
        @Test
        @DisplayName("skipBonus ends current player's turn")
        void skipEndsTurn() {
            Game game = game(2);
            Board board = game.getBoard();

            Player first = game.getPlayers().get(0);
            Player second = game.getPlayers().get(1);

            removeAllPickableCharacters(board);
            giveFood(first, 20);

            board.placeTotem(1, first);
            board.placeTotem(2, second);

            ActionState state = actionState(game);
            state.start();

            state.skipBonus(first);

            assertEquals(second.getNickname(), state.getActivePlayerNickname());
        }
    }
}
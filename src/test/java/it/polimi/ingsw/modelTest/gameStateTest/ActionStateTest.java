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

        @Test
        @DisplayName("active player is null before start")
        void activePlayerNullBeforeStart() {
            Game game = game(2);
            ActionState state = actionState(game);

            assertNull(state.getActivePlayerNickname());
        }

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
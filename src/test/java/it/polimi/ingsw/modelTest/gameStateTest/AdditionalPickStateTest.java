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
        player.addCard(new LatePurchase(9001, 0, 0, 3));
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

        @Test
        @DisplayName("when no player has bonus picks, state transitions to RoundEndState")
        void transitionsToRoundEndStateWhenNoBonusPicks() {
            Game game = game(2);

            AdditionalPickState state = additionalPickState(game);
            state.start();

            assertFalse(game.getCurrentState() instanceof AdditionalPickState);
        }

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

        @Test
        @DisplayName("cannot take event card with bonus pick")
        void cannotTakeEventCard() {
            Game game = game(2);
            Board board = game.getBoard();
            Player player = game.getPlayers().get(0);

            giveBonusPick(player);

            board.addTopRow(new Hunt(9002, 1, 1, 1));
            int eventIdx = board.getRow(0).size() - 1;

            AdditionalPickState state = additionalPickState(game);
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
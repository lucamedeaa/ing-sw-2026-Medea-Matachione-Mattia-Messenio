package it.polimi.ingsw.modelTest.gameStateTest;

import it.polimi.ingsw.modelTest.ModelTest;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.OfferTile;
import it.polimi.ingsw.server.model.exception.InvalidGameActionException;
import it.polimi.ingsw.server.model.state.ActionState;
import it.polimi.ingsw.server.model.state.PlacementState;
import it.polimi.ingsw.server.model.update.AvailableAction;
import it.polimi.ingsw.server.model.update.AvailableAction.PlaceTotemAction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlacementStateTest extends ModelTest {

    private Game game(int n) {
        return new Game(playerNames(n));
    }

    private List<String> playerNames(int n) {
        return java.util.stream.IntStream.rangeClosed(1, n)
                .mapToObj(i -> "Player" + i)
                .toList();
    }

    private PlacementState placementState(Game game) {
        return new PlacementState(game);
    }

    @Nested
    @DisplayName("PlacementState start")
    class StartTests {

        @Test
        @DisplayName("start does not throw")
        void startDoesNotThrow() {
            Game game = game(2);
            PlacementState state = placementState(game);

            assertDoesNotThrow(state::start);
        }
    }

    @Nested
    @DisplayName("Active player")
    class ActivePlayerTests {

        @Test
        @DisplayName("getActivePlayerNickname returns current board player")
        void getActivePlayerNicknameReturnsCurrentPlayer() {
            Game game = game(2);
            Board board = game.getBoard();

            PlacementState state = placementState(game);

            assertEquals(board.getCurrentPlayer().getNickname(), state.getActivePlayerNickname());
        }

        @Test
        @DisplayName("getActivePlayerNickname returns null when all totems are placed")
        void getActivePlayerNicknameReturnsNullWhenAllTotemsPlaced() {
            Game game = game(2);
            Board board = game.getBoard();

            board.consumeCurrentPlayer();
            board.consumeCurrentPlayer();

            PlacementState state = placementState(game);

            assertNull(state.getActivePlayerNickname());
        }
    }

    @Nested
    @DisplayName("Available actions")
    class AvailableActionsTests {

        @Test
        @DisplayName("active player has PlaceTotemAction")
        void activePlayerHasPlaceTotemAction() {
            Game game = game(2);
            Board board = game.getBoard();

            Player current = board.getCurrentPlayer();

            PlacementState state = placementState(game);

            List<AvailableAction> actions = state.getAvailableActions(current.getNickname());

            assertEquals(1, actions.size());
            assertTrue(actions.get(0) instanceof PlaceTotemAction);
        }

        @Test
        @DisplayName("inactive player has no available actions")
        void inactivePlayerHasNoActions() {
            Game game = game(2);
            Board board = game.getBoard();

            Player current = board.getCurrentPlayer();
            Player inactive = game.getPlayers().stream()
                    .filter(p -> !p.equals(current))
                    .findFirst()
                    .orElseThrow();

            PlacementState state = placementState(game);

            assertTrue(state.getAvailableActions(inactive.getNickname()).isEmpty());
        }

        @Test
        @DisplayName("PlaceTotemAction contains only free tiles")
        void placeTotemActionContainsOnlyFreeTiles() {
            Game game = game(2);
            Board board = game.getBoard();

            Player current = board.getCurrentPlayer();
            Player other = game.getPlayers().stream()
                    .filter(p -> !p.equals(current))
                    .findFirst()
                    .orElseThrow();

            board.placeTotem(0, other);

            PlacementState state = placementState(game);

            PlaceTotemAction action = (PlaceTotemAction) state.getAvailableActions(current.getNickname()).get(0);

            List<OfferTile> track = board.getOfferTrack();
            for (int tileIndex : action.availableTileIndices()) {
                assertTrue(track.get(tileIndex).isFree());
            }

            assertFalse(action.availableTileIndices().contains(0));
        }
    }

    @Nested
    @DisplayName("placeTotem validation")
    class PlaceTotemValidation {

        @Test
        @DisplayName("wrong player cannot place totem")
        void wrongPlayerCannotPlaceTotem() {
            Game game = game(2);
            Board board = game.getBoard();

            Player current = board.getCurrentPlayer();
            Player wrong = game.getPlayers().stream()
                    .filter(p -> !p.equals(current))
                    .findFirst()
                    .orElseThrow();

            PlacementState state = placementState(game);

            assertThrows(InvalidGameActionException.class,
                    () -> state.placeTotem(wrong, 0));
        }

        @Test
        @DisplayName("cannot place totem on occupied tile")
        void cannotPlaceTotemOnOccupiedTile() {
            Game game = game(2);
            Board board = game.getBoard();

            Player current = board.getCurrentPlayer();
            board.placeTotem(0, game.getPlayers().stream()
                    .filter(p -> !p.equals(current))
                    .findFirst()
                    .orElseThrow());

            PlacementState state = placementState(game);

            assertThrows(InvalidGameActionException.class,
                    () -> state.placeTotem(current, 0));
        }

        @Test
        @DisplayName("cannot place totem on invalid tile")
        void cannotPlaceTotemOnInvalidTile() {
            Game game = game(2);
            Board board = game.getBoard();

            Player current = board.getCurrentPlayer();
            int invalidIndex = board.getOfferTrack().size();

            PlacementState state = placementState(game);

            assertThrows(InvalidGameActionException.class,
                    () -> state.placeTotem(current, invalidIndex));
        }
    }

    @Nested
    @DisplayName("placeTotem effects")
    class PlaceTotemEffects {

        @Test
        @DisplayName("placeTotem occupies selected tile")
        void placeTotemOccupiesSelectedTile() {
            Game game = game(2);
            Board board = game.getBoard();

            Player current = board.getCurrentPlayer();

            PlacementState state = placementState(game);
            state.placeTotem(current, 0);

            assertFalse(board.getOfferTrack().get(0).isFree());
        }

        @Test
        @DisplayName("placeTotem advances to next player")
        void placeTotemAdvancesToNextPlayer() {
            Game game = game(2);
            Board board = game.getBoard();

            Player first = board.getCurrentPlayer();

            PlacementState state = placementState(game);
            state.placeTotem(first, 0);

            assertNotEquals(first.getNickname(), state.getActivePlayerNickname());
        }

        @Test
        @DisplayName("after all totems are placed, game transitions to ActionState")
        void transitionsToActionStateWhenAllTotemsArePlaced() {
            Game game = game(2);
            Board board = game.getBoard();

            PlacementState state = placementState(game);

            Player first = board.getCurrentPlayer();
            state.placeTotem(first, 0);

            Player second = board.getCurrentPlayer();
            state.placeTotem(second, 1);

            assertTrue(game.getCurrentState() instanceof ActionState);
        }
    }
}
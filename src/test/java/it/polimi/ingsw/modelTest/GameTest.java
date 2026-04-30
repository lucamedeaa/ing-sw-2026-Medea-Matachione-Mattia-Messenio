package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.network.dto.AvailableActionDTO;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.ModelUpdateDTO;
import it.polimi.ingsw.network.dto.PlayerDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest extends ModelTest {

    // ── Helper

    private Game game(int n) {
        List<String> names = new java.util.ArrayList<>();
        for (int i = 1; i <= n; i++) names.add("Player" + i);
        return new Game(names);
    }

    // ── Construction

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("2-player game initialises without exceptions")
        void twoPlayerGameInitialises() {
            assertDoesNotThrow(() -> game(2));
        }

        @Test
        @DisplayName("5-player game initialises without exceptions")
        void fivePlayerGameInitialises() {
            assertDoesNotThrow(() -> game(5));
        }

        @Test
        @DisplayName("1 player throws IllegalArgumentException")
        void onePlayerThrows() {
            assertThrows(IllegalArgumentException.class, () -> game(1));
        }

        @Test
        @DisplayName("6 players throws IllegalArgumentException")
        void sixPlayersThrows() {
            assertThrows(IllegalArgumentException.class, () -> game(6));
        }

        @Test
        @DisplayName("null players throws IllegalArgumentException")
        void nullPlayersThrows() {
            assertThrows(IllegalArgumentException.class, () -> new Game(null));
        }

        @Test
        @DisplayName("getPlayers returns correct number of players")
        void getPlayersCorrectSize() {
            assertEquals(3, game(3).getPlayers().size());
        }

        @Test
        @DisplayName("getBoard returns non-null board")
        void getBoardNonNull() {
            assertNotNull(game(2).getBoard());
        }

        @Test
        @DisplayName("getCurrentRound starts at 1")
        void initialRoundIsOne() {
            assertEquals(1, game(2).getCurrentRound());
        }
    }

    // ── incrementRound

    @Nested
    @DisplayName("Round management")
    class RoundManagement {

        @Test
        @DisplayName("incrementRound increases round by 1")
        void incrementRoundByOne() {
            Game g = game(2);
            g.incrementRound();
            assertEquals(2, g.getCurrentRound());
        }

        @Test
        @DisplayName("incrementRound multiple times accumulates correctly")
        void incrementRoundMultiple() {
            Game g = game(2);
            g.incrementRound();
            g.incrementRound();
            assertEquals(3, g.getCurrentRound());
        }
    }

    // ── getPlayerByNickname

    @Nested
    @DisplayName("getPlayerByNickname")
    class GetPlayerByNickname {

        @Test
        @DisplayName("returns correct player for existing nickname")
        void returnsCorrectPlayer() {
            Game g = game(3);
            assertEquals("Player1", g.getPlayerByNickname("Player1").getNickname());
        }

        @Test
        @DisplayName("throws IllegalArgumentException for unknown nickname")
        void throwsForUnknownNickname() {
            Game g = game(2);
            assertThrows(IllegalArgumentException.class, () -> g.getPlayerByNickname("Unknown"));
        }
    }

    // ── Observer

    @Nested
    @DisplayName("Observer notifications")
    class ObserverNotifications {

        @Test
        @DisplayName("addObserver + notifyFullSync calls onFullSync on observer")
        void fullSyncNotifiesObserver() {
            Game g = game(2);
            boolean[] called = {false};
            g.addObserver(new ModelObserver() {
                @Override public void onModelUpdate(ModelUpdateDTO update) {}
                @Override public void onFullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {
                    called[0] = true;
                }
            });
            g.notifyFullSync();
            assertTrue(called[0]);
        }

        @Test
        @DisplayName("notifyFullSync passes correct number of players to observer")
        void fullSyncPassesCorrectPlayers() {
            Game g = game(3);
            int[] count = {0};
            g.addObserver(new ModelObserver() {
                @Override public void onModelUpdate(ModelUpdateDTO update) {}
                @Override public void onFullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {
                    count[0] = players.size();
                }
            });
            g.notifyFullSync();
            assertEquals(3, count[0]);
        }
    }

    // ── start

    @Nested
    @DisplayName("start")
    class Start {

        @Test
        @DisplayName("start does not throw")
        void startDoesNotThrow() {
            Game g = game(2);
            g.addObserver(new ModelObserver() {
                @Override public void onModelUpdate(ModelUpdateDTO update) {}
                @Override public void onFullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {}
            });
            assertDoesNotThrow(g::start);
        }

        @Test
        @DisplayName("after start, getCurrentState is non-null")
        void afterStartStateNonNull() {
            Game g = game(2);
            g.addObserver(new ModelObserver() {
                @Override public void onModelUpdate(ModelUpdateDTO update) {}
                @Override public void onFullSync(BoardDTO board, List<PlayerDTO> players, String activePlayer, List<AvailableActionDTO> actions) {}
            });
            g.start();
            assertNotNull(g.getCurrentState());
        }
    }
}
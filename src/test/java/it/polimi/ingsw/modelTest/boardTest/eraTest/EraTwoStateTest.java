package it.polimi.ingsw.modelTest.boardTest.eraTest;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.era.EraThreeState;
import it.polimi.ingsw.server.model.board.era.EraTwoState;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.server.model.card.Card;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class EraTwoStateTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that EraTwoState correctly reports its era number as 2.
     *
     * EXPECTATION:
     * getEraNumber() returns 2.
     */
    @Test
    @DisplayName("getEraNumber returns 2")
    void eraNumberIsTwo() {
        assertEquals(2, new EraTwoState().getEraNumber());
    }

    /**
     * SUMMARY:
     * Verifies that the next era after EraTwoState is an EraThreeState instance.
     *
     * EXPECTATION:
     * getNextEra() returns an object that is an instance of EraThreeState.
     */
    @Test
    @DisplayName("getNextEra returns an EraThreeState")
    void nextEraIsEraThree() {
        assertInstanceOf(EraThreeState.class, new EraTwoState().getNextEra());
    }

    /**
     * SUMMARY:
     * Verifies that transitionSetup for EraTwoState completes without throwing any exception.
     *
     * EXPECTATION:
     * No exception is thrown when transitionSetup is called on a 2-player board.
     */
    @Test
    @DisplayName("transitionSetup does not throw")
    void transitionSetupDoesNotThrow() {
        Board board = new Board(2, newPlayers(2));
        assertDoesNotThrow(() -> new EraTwoState().transitionSetup(board));
    }
    /**
     * SUMMARY:
     * Verifies that transitionSetup for EraTwoState properly shifts existing buildings and adds new era 2 buildings to the upper row.
     *
     * EXPECTATION:
     * The upper row contains at least one persistent card with era == 2 after transition.
     */
    @Test
    @DisplayName("transitionSetup shifts buildings and adds era two buildings")
    void transitionSetupShiftsBuildingsAndAddsNewEraBuildings() throws Exception {
        Board board = new Board(2, newPlayers(2));

        java.lang.reflect.Field field = Board.class.getDeclaredField("currentEraState");
        field.setAccessible(true);
        field.set(board, new EraTwoState());

        new EraTwoState().transitionSetup(board);

        boolean hasEra2Buildings = board.getRow(0).stream()
                .flatMap(Optional::stream)
                .filter(Card::isPersistent)
                .anyMatch(c -> c.getEra() == 2);

        assertTrue(hasEra2Buildings, "Upper row should contain era 2 buildings");
    }
}
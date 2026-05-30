package it.polimi.ingsw.modelTest.boardTest.eraTest;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.era.EraOneState;
import it.polimi.ingsw.server.model.board.era.EraTwoState;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EraOneStateTest extends ModelTest {

    /**
     * SUMMARY:
     * Verifies that EraOneState correctly reports its era number as 1.
     *
     * EXPECTATION:
     * getEraNumber() returns 1.
     */
    @Test
    @DisplayName("getEraNumber returns 1")
    void eraNumberIsOne() {
        assertEquals(1, new EraOneState().getEraNumber());
    }

    /**
     * SUMMARY:
     * Verifies that the next era after EraOneState is an EraTwoState instance.
     *
     * EXPECTATION:
     * getNextEra() returns an object that is an instance of EraTwoState.
     */
    @Test
    @DisplayName("getNextEra returns an EraTwoState")
    void nextEraIsEraTwo() {
        assertInstanceOf(EraTwoState.class, new EraOneState().getNextEra());
    }

    /**
     * SUMMARY:
     * Verifies that transitionSetup for EraOneState completes without throwing any exception.
     *
     * EXPECTATION:
     * No exception is thrown when transitionSetup is called on a 2-player board.
     */
    @Test
    @DisplayName("transitionSetup does not throw")
    void transitionSetupDoesNotThrow() {
        assertDoesNotThrow(() -> new EraOneState().transitionSetup(new Board(2, newPlayers(2))));
    }
}
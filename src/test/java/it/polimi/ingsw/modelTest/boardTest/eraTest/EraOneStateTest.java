package it.polimi.ingsw.modelTest.boardTest.eraTest;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.era.EraOneState;
import it.polimi.ingsw.server.model.board.era.EraTwoState;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EraOneStateTest extends ModelTest {

    @Test
    @DisplayName("getEraNumber returns 1")
    void eraNumberIsOne() {
        assertEquals(1, new EraOneState().getEraNumber());
    }

    @Test
    @DisplayName("getNextEra returns an EraTwoState")
    void nextEraIsEraTwo() {
        assertInstanceOf(EraTwoState.class, new EraOneState().getNextEra());
    }

    @Test
    @DisplayName("transitionSetup does not throw")
    void transitionSetupDoesNotThrow() {
        assertDoesNotThrow(() -> new EraOneState().transitionSetup(new Board(2, newPlayers(2))));
    }
}
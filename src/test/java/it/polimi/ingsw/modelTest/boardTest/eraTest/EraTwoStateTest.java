package it.polimi.ingsw.modelTest.boardTest.eraTest;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.era.EraThreeState;
import it.polimi.ingsw.server.model.board.era.EraTwoState;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EraTwoStateTest extends ModelTest {

    @Test
    @DisplayName("getEraNumber returns 2")
    void eraNumberIsTwo() {
        assertEquals(2, new EraTwoState().getEraNumber());
    }

    @Test
    @DisplayName("getNextEra returns an EraThreeState")
    void nextEraIsEraThree() {
        assertInstanceOf(EraThreeState.class, new EraTwoState().getNextEra());
    }

    @Test
    @DisplayName("transitionSetup does not throw")
    void transitionSetupDoesNotThrow() {
        Board board = new Board(2, newPlayers(2));
        assertDoesNotThrow(() -> new EraTwoState().transitionSetup(board));
    }
}
package it.polimi.ingsw.modelTest.boardTest.eraTest;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Era.EraThreeState;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EraThreeStateTest extends ModelTest {

    @Test
    @DisplayName("getEraNumber returns 3")
    void eraNumberIsThree() {
        assertEquals(3, new EraThreeState().getEraNumber());
    }

    @Test
    @DisplayName("getNextEra returns itself (era 3 is terminal)")
    void nextEraIsItself() {
        EraThreeState era = new EraThreeState();
        assertSame(era, era.getNextEra());
    }

    @Test
    @DisplayName("transitionSetup does not throw")
    void transitionSetupDoesNotThrow() {
        Board board = new Board(2, newPlayers(2));
        assertDoesNotThrow(() -> new EraThreeState().transitionSetup(board));
    }
}
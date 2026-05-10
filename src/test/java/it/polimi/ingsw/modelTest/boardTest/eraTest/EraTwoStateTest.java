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
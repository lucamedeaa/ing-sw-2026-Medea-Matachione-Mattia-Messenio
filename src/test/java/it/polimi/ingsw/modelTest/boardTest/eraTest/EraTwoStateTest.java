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
    @DisplayName("transitionSetup moves era 1 buildings to lower row and adds era 2 buildings to upper row")
    void transitionSetupShiftsBuildingsAndAddsNewEraBuildings() {
        Board board = new Board(2, newPlayers(2));

        long era1BuildingsInUpper = board.getRow(0).stream()
                .flatMap(Optional::stream)
                .filter(Card::isPersistent)
                .count();

        new EraTwoState().transitionSetup(board);

        long buildingsInLower = board.getRow(1).stream()
                .flatMap(Optional::stream)
                .filter(Card::isPersistent)
                .count();
        long buildingsInUpper = board.getRow(0).stream()
                .flatMap(Optional::stream)
                .filter(Card::isPersistent)
                .count();

        assertTrue(buildingsInLower >= era1BuildingsInUpper,
                "Era 1 buildings should have moved to lower row");
        assertTrue(buildingsInUpper > 0,
                "Upper row should contain era 2 buildings");
    }
}
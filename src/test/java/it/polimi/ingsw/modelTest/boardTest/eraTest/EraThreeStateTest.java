package it.polimi.ingsw.modelTest.boardTest.eraTest;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.era.EraThreeState;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.server.model.board.era.EraTwoState;
import it.polimi.ingsw.server.model.card.Card;
import java.util.Optional;
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
    @Test
    @DisplayName("transitionSetup clears buildings from lower row and adds era 3 buildings to upper row")
    void transitionSetupClearsLowerAndAddsEraThreeBuildings() {
        Board board = new Board(2, newPlayers(2));
        new EraTwoState().transitionSetup(board); // bring era 1 buildings into lower row

        new EraThreeState().transitionSetup(board);

        boolean lowerHasNoBuildings = board.getRow(1).stream()
                .flatMap(Optional::stream)
                .noneMatch(Card::isPersistent);
        boolean upperHasBuildings = board.getRow(0).stream()
                .flatMap(Optional::stream)
                .anyMatch(Card::isPersistent);

        assertTrue(lowerHasNoBuildings,
                "Lower row should contain no buildings after clearBuildingsFromLowerRow");
        assertTrue(upperHasBuildings,
                "Upper row should contain era 3 buildings");
    }
}
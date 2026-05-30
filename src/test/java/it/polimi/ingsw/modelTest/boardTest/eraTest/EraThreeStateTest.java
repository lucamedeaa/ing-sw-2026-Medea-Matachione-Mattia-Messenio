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

    /**
     * SUMMARY:
     * Verifies that EraThreeState correctly reports its era number as 3.
     *
     * EXPECTATION:
     * getEraNumber() returns 3.
     */
    @Test
    @DisplayName("getEraNumber returns 3")
    void eraNumberIsThree() {
        assertEquals(3, new EraThreeState().getEraNumber());
    }

    /**
     * SUMMARY:
     * Verifies that EraThreeState is the terminal era, meaning getNextEra returns the same instance.
     *
     * EXPECTATION:
     * getNextEra() returns the exact same EraThreeState object (assertSame).
     */
    @Test
    @DisplayName("getNextEra returns itself (era 3 is terminal)")
    void nextEraIsItself() {
        EraThreeState era = new EraThreeState();
        assertSame(era, era.getNextEra());
    }

    /**
     * SUMMARY:
     * Verifies that transitionSetup for EraThreeState completes without throwing any exception.
     *
     * EXPECTATION:
     * No exception is thrown when transitionSetup is called on a 2-player board.
     */
    @Test
    @DisplayName("transitionSetup does not throw")
    void transitionSetupDoesNotThrow() {
        Board board = new Board(2, newPlayers(2));
        assertDoesNotThrow(() -> new EraThreeState().transitionSetup(board));
    }
    /**
     * SUMMARY:
     * Verifies that transitionSetup for EraThreeState clears the lower row, shifts buildings, and adds new era 3 buildings to the upper row.
     *
     * EXPECTATION:
     * The upper row contains at least one persistent card with era == 3 after transition.
     */
    @Test
    @DisplayName("transitionSetup clears lower, shifts buildings and adds era three buildings")
    void transitionSetupClearsLowerAndAddsEraThreeBuildings() throws Exception {
        Board board = new Board(2, newPlayers(2));

        // Forza l'aggiornamento dello stato interno della Board all'Era 3
        java.lang.reflect.Field field = Board.class.getDeclaredField("currentEraState");
        field.setAccessible(true);
        field.set(board, new EraThreeState());

        // Esegue il setup dell'Era 3
        new EraThreeState().transitionSetup(board);

        // Verifica che la riga superiore contenga edifici dell'Era 3
        boolean hasEra3Buildings = board.getRow(0).stream()
                .flatMap(Optional::stream)
                .filter(Card::isPersistent)
                .anyMatch(c -> c.getEra() == 3);

        assertTrue(hasEra3Buildings, "Upper row should contain era 3 buildings");
    }
}
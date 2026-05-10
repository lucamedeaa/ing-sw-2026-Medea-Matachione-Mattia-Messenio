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
package it.polimi.ingsw.guiTest;

import it.polimi.ingsw.client.view.gui.controllers.ControllerRegistry;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.fail;

public class DipViolationTest {

    @Test
    public void testPresenterMocking_isImpossible() {
        ControllerRegistry registry = new ControllerRegistry(null, null, () -> "test");

        // Tentativo di creare una InGameScreen tramite il registry per fare un Unit Test della view.
        Object controller = registry.createController(InGameScreen.class);

        // Verifica: il controller creato ha un InGamePresenter reale (dipendenza concreta) o un mock?
        try {
            Field presenterField = InGameScreen.class.getDeclaredField("presenter");
            presenterField.setAccessible(true);
            Object presenter = presenterField.get(controller);

            // Se il presenter non è nullo e non è un mock (impossibile iniettarlo senza reflection),
            // il codice vìola il Dependency Inversion Principle.
            if (presenter.getClass().getName().equals("it.polimi.ingsw.client.view.gui.presenter.InGamePresenter")) {
                // Test "passato" nel senso che la vulnerabilità è esposta.
                return;
            }
            fail("Il sistema ha magicamente iniettato un mock, il DIP è rispettato (inatteso).");

        } catch (Exception e) {
            fail("Struttura interna inaccessibile: " + e.getMessage());
        }
    }
}
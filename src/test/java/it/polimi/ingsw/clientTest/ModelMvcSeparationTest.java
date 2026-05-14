package it.polimi.ingsw.clientTest;

import it.polimi.ingsw.client.model.EventApplier;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.common.network.dto.event.EraTransitionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class ModelMvcSeparationTest {

    @Nested
    @DisplayName("Model and View Separation")
    class Separation {

        @Test
        @DisplayName("Game logs strictly contain data, without UI-specific ANSI escape codes")
        void testLogsAreAnsiFree() {
            GameModel model = new GameModel();
            EventApplier applier = new EventApplier(model);

            // Il server comunica un cambio di Era
            applier.visit(new EraTransitionDto(2));

            // Leggiamo il log generato nel modello
            List<String> logs = model.consumeGameLogs();
            assertFalse(logs.isEmpty(), "Il log dovrebbe essere stato registrato.");
            String generatedLog = logs.get(0);

            // Verifica: Il log NON deve contenere i caratteri di escape per i colori (\033[)
            // Se fallisce, il Model è accoppiato alla TUI e darà problemi alla GUI.
            assertFalse(generatedLog.contains("\033["),
                    "VULNERABILITA' MVC: Il modello sta salvando stringhe formattate con " +
                            "codici ANSI per il terminale. I colori vanno aggiunti dai Renderer della TUI.");
        }
    }
}
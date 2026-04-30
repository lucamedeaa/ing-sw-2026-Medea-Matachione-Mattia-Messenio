package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.network.dto.AvailableActionDTO;

import java.util.List;

public class InGameState implements UIState {
    private final TUI tui;

    public InGameState(TUI tui) {
        this.tui = tui;
    }

    @Override
    public void render() {
        tui.renderInGame();

        List<AvailableActionDTO> actions = tui.getModel().getMyActions();

        tui.print("\n--- AVAILABLE ACTIONS ---");
        if (actions.isEmpty()) {
            tui.print("Wait for your turn...");
            return;
        }

        ActionRender renderer = new ActionRender(tui);
        for (int i = 0; i < actions.size(); i++) {
            tui.prompt(i + ") ");
            actions.get(i).accept(renderer);
        }
        tui.prompt("> (Format: <action_id> [parameters...])\n> ");
    }

    @Override
    public void handleInput(String input) {
        List<AvailableActionDTO> actions = tui.getModel().getMyActions();

        // 1. Errore se non è il tuo turno
        if (actions.isEmpty()) {
            String errorMsg = "Non è il tuo turno! Attendi che gli altri giocatori finiscano.";
            System.err.println("[CLIENT ERROR] " + errorMsg); // <-- QUESTO STAMPA IL MESSAGGIO ROSSO
            onError(errorMsg);
            return;
        }

        try {
            String[] parts = input.trim().split("\\s+");
            int actionIndex = Integer.parseInt(parts[0]);

            if (actionIndex < 0 || actionIndex >= actions.size()) {
                String errorMsg = "Azione non valida. Scegli un numero dalla lista.";
                System.err.println("[CLIENT ERROR] " + errorMsg);
                onError(errorMsg);
                return;
            }

            AvailableActionDTO selectedAction = actions.get(actionIndex);
            ActionExecutor executor = new ActionExecutor(tui, parts);
            selectedAction.accept(executor);

        } catch (NumberFormatException e) {
            String errorMsg = "Formato non valido. Devi inserire un numero.";
            System.err.println("[CLIENT ERROR] " + errorMsg);
            onError(errorMsg);
        } catch (Exception e) {
            String errorMsg = "Errore di input: " + e.getMessage();
            System.err.println("[CLIENT ERROR] " + errorMsg);
            onError(errorMsg);
        }
    }
    @Override
    public void onError(String errorText) {
        tui.print("\n[ERRORE DAL SERVER]: " + errorText);
        render();
    }
    @Override
    public void onModelUpdated() {
        render();
    }
}
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
        tui.renderInGame(tui.getModel().getMyActions());
    }

    @Override
    public void handleInput(String input) {
        List<AvailableActionDTO> actions = tui.getModel().getMyActions();

        if (actions.isEmpty()) {
            onError("It's not your turn! Wait for the other players.");
            return;
        }

        try {
            String[] parts = input.trim().split("\\s+");
            int actionIndex = Integer.parseInt(parts[0]);

            if (actionIndex < 0 || actionIndex >= actions.size()) {
                onError("Invalid action. Pick a number from the list.");
                return;
            }

            AvailableActionDTO selectedAction = actions.get(actionIndex);
            ActionExecutor executor = new ActionExecutor(tui, parts);
            selectedAction.accept(executor);

        } catch (NumberFormatException e) {
            onError("Invalid format. You must enter a number.");
        } catch (Exception e) {
            onError("Input error: " + e.getMessage());
        }
    }

    @Override
    public void onError(String errorText) {
        tui.print("[ERROR]: " + errorText);
        render();
    }

    @Override
    public void onModelUpdated() {
        render();
    }
}
package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.network.dto.AvailableActionDTO;

import java.util.List;

public class InGameState implements UIState {
    private final TUI tui;
    List<AvailableActionDTO> actions;

    public InGameState(TUI tui) {
        this.tui = tui;
    }

    @Override
    public void render() {
        actions = tui.getModel().getMyActions();
        tui.renderInGame(actions);
    }

    @Override
    public void handleInput(String input) {
        actions = tui.getModel().getMyActions();
        if (actions.isEmpty()) return;

        try {
            String[] parts = input.trim().split("\\s+");
            int actionIndex = Integer.parseInt(parts[0]);

            if (actionIndex < 0 || actionIndex >= actions.size()) {
                tui.print("Invalid action index.");
                return;
            }

            AvailableActionDTO selectedAction = actions.get(actionIndex);
            ActionExecutor executor = new ActionExecutor(tui, parts);

            selectedAction.accept(executor);

        } catch (NumberFormatException e) {
            tui.print("Invalid input format. Use numbers.");
        } catch (Exception e) {
            tui.print("Input error: " + e.getMessage());
        }
    }

    @Override
    public void onModelUpdated() {
        render();
    }
}
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
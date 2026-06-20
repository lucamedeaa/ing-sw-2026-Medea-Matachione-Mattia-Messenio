package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.tui.OutputPort;

import java.util.List;

/**
 * Command that executes one of the currently available game actions.
 */
public class ActionCommand implements GameCommand {
    private final GameModel gameModel;
    private final ServerCommandPort controller;
    private final OutputPort out;
    private final String[] args;

    /**
     * Creates an action command from the tokenized TUI input.
     *
     * @param gameModel model containing the available actions and board data
     * @param controller port used to send the selected action to the server
     * @param out output port used for validation feedback
     * @param args tokenized command arguments
     */
    public ActionCommand(GameModel gameModel, ServerCommandPort controller, OutputPort out, String[] args) {
        this.gameModel = gameModel;
        this.controller = controller;
        this.out = out;
        this.args = args;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        var availableActions = gameModel.getMyActions();

        if (availableActions.isEmpty()) {
            String active = gameModel.getActivePlayer();
            String target = (active == null) ? "the current phase" : active;
            gameModel.setGlobalError("It's not your turn! Wait until " + target + " has finished.");
            return;
        }

        try {
            int actionIndex = Integer.parseInt(args[0]);

            if (actionIndex < 0 || actionIndex >= availableActions.size()) {
                gameModel.setGlobalError("Invalid action index. Choose between 0 and " + (availableActions.size() - 1) + ".");
                return;
            }

            var selectedAction = availableActions.get(actionIndex);

            ActionExecutor executor = new ActionExecutor(controller, gameModel, out, args);
            selectedAction.accept(executor);

        } catch (NumberFormatException e) {
            gameModel.setGlobalError("'" + args[0] + "' this is not a valid number.");
        }
    }
}

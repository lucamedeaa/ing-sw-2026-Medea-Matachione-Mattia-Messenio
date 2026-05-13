package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.tui.OutputPort;

import java.util.List;

public class ActionCommand implements GameCommand {
    private final GameModel gameModel;
    private final ServerCommandPort controller;
    private final OutputPort out;
    private final String[] args;

    public ActionCommand(GameModel gameModel, ServerCommandPort controller, OutputPort out, String[] args) {
        this.gameModel = gameModel;
        this.controller = controller;
        this.out = out;
        this.args = args;
    }

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
                gameModel.setGlobalError("Invalid action index.");
                return;
            }

            var selectedAction = availableActions.get(actionIndex);

            ActionExecutor executor = new ActionExecutor(controller, gameModel, out, args);
            selectedAction.accept(executor);

            //if(executor.isCommandSent()){
              //  gameModel.setAvailableActions(List.of());
            //}

        } catch (NumberFormatException e) {
            gameModel.setGlobalError("'" + args[0] + "' this is not a valid number.");
        }
    }
}
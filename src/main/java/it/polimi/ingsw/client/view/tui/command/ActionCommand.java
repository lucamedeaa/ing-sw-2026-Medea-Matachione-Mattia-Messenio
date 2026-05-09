package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.tui.OutputPort;

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
            gameModel.setGlobalError("Non è il tuo turno! Aspetta che " + gameModel.getActivePlayer() + " finisca la sua mossa.");
            return;
        }

        try {
            int actionIndex = Integer.parseInt(args[0]);

            if (actionIndex < 0 || actionIndex >= availableActions.size()) {
                gameModel.setGlobalError("Indice azione non valido.");
                return;
            }

            var selectedAction = availableActions.get(actionIndex);


            ActionExecutor executor = new ActionExecutor(controller, gameModel, out, args);
            selectedAction.accept(executor);

        } catch (NumberFormatException e) {
            gameModel.setGlobalError("'" + args[0] + "' non è un numero valido.");
        }
    }
}
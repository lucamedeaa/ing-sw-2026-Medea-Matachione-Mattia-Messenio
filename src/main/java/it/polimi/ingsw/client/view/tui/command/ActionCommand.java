package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.NavigationPort;
import it.polimi.ingsw.client.view.tui.OutputPort;

public class ActionCommand implements GameCommand {
    private final NavigationPort nav;
    private final OutputPort out;
    private final String[] args;

    public ActionCommand(NavigationPort nav, OutputPort out, String[] args) {
        this.nav = nav;
        this.out = out;
        this.args = args;
    }

    @Override
    public void execute() {
        // Uso nav per accedere al Model
        var availableActions = nav.getMatchModel().getMyActions();

        if (availableActions.isEmpty()) {
            nav.getMatchModel().setGlobalError("Non è il tuo turno! Aspetta che " + nav.getMatchModel().getActivePlayer() + " finisca la sua mossa.");
            return;
        }

        try {
            int actionIndex = Integer.parseInt(args[0]);

            if (actionIndex < 0 || actionIndex >= availableActions.size()) {
                nav.getMatchModel().setGlobalError("Indice azione non valido.");
                return;
            }

            var selectedAction = availableActions.get(actionIndex);


            ActionExecutor executor = new ActionExecutor(nav.getController(), nav.getMatchModel(), out, args);
            selectedAction.accept(executor);

        } catch (NumberFormatException e) {
            nav.getMatchModel().setGlobalError("'" + args[0] + "' non è un numero valido.");
        }
    }
}
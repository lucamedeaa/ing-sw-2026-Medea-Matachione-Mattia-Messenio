package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;

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
        var availableActions = nav.getModel().getMyActions();

        if (availableActions.isEmpty()) {
            // Uso out per stampare
            out.print("\033[33m[ATTENZIONE] Non è il tuo turno! Aspetta che " +
                    nav.getModel().getActivePlayer() + " finisca la sua mossa.\033[0m");
            return;
        }

        try {
            int actionIndex = Integer.parseInt(args[0]);

            if (actionIndex < 0 || actionIndex >= availableActions.size()) {
                out.print("\033[31m[ERRORE] Indice non valido.\033[0m");
                return;
            }

            var selectedAction = availableActions.get(actionIndex);


            ActionExecutor executor = new ActionExecutor(nav, out, args);
            selectedAction.accept(executor);

        } catch (NumberFormatException e) {
            out.print("\033[31m[ERRORE] '" + args[0] + "' non è un numero valido.\033[0m");
        }
    }
}
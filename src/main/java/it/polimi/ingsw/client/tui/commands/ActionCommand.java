package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.render.ActionExecutor;

public class ActionCommand implements GameCommand {
    private final TUI tui;
    private final String[] args;

    public ActionCommand(TUI tui, String[] args) {
        this.tui = tui;
        this.args = args;
    }

    @Override
    public void execute() {
        // 1. Recupero le azioni disponibili dal Model
        var availableActions = tui.getModel().getMyActions();

        // 2. Controllo se è il turno del giocatore
        if (availableActions.isEmpty()) {
            tui.print("\033[33m[ATTENZIONE] Non è il tuo turno! Aspetta che " +
                    tui.getModel().getActivePlayer() + " finisca la sua mossa.\033[0m");
            return;
        }

        try {
            int actionIndex = Integer.parseInt(args[0]);

            // 3. Controllo se l'indice è presente nella lista
            if (actionIndex < 0 || actionIndex >= availableActions.size()) {
                tui.print("\033[31m[ERRORE] Indice " + actionIndex +
                        " non valido. Scegli un numero tra 0 e " + (availableActions.size() - 1) + ".\033[0m");
                return;
            }

            // 4. Esecuzione tramite Visitor
            var selectedAction = availableActions.get(actionIndex);
            ActionExecutor executor = new ActionExecutor(tui, args);
            selectedAction.accept(executor);

        } catch (NumberFormatException e) {
            tui.print("\033[31m[ERRORE] '" + args[0] + "' non è un numero valido.\033[0m");
        }
    }
}

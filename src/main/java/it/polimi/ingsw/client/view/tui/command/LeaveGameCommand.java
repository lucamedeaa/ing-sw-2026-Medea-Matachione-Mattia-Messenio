package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.OutputPort;

public class LeaveGameCommand implements GameCommand {
    private final ServerCommandPort controller;
    private final OutputPort out;

    public LeaveGameCommand(ServerCommandPort controller, OutputPort out) {
        this.controller = controller;
        this.out = out;
    }

    @Override
    public void execute() {
        controller.leaveGame(); // o leaveMatch() se hai implementato la separazione
        out.print("Richiesta di uscita inviata...");
    }
}
package it.polimi.ingsw.client.tui.commands;

import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.OutputPort;

public class LeaveGameCommand implements GameCommand {
    private final ServerController controller;
    private final OutputPort out;

    public LeaveGameCommand(ServerController controller, OutputPort out) {
        this.controller = controller;
        this.out = out;
    }

    @Override
    public void execute() {
        controller.leaveGame(); // o leaveMatch() se hai implementato la separazione
        out.print("Richiesta di uscita inviata...");
    }
}
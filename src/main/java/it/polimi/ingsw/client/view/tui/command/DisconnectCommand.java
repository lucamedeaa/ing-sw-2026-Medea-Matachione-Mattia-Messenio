package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.ApplicationLifecyclePort;
import it.polimi.ingsw.client.view.tui.OutputPort;

public class DisconnectCommand implements GameCommand {
    private final ServerCommandPort controller;
    private final ApplicationLifecyclePort lifecyclePort;
    private final OutputPort out;

    public DisconnectCommand(ServerCommandPort controller, ApplicationLifecyclePort lifecyclePort, OutputPort out) {
        this.controller = controller;
        this.lifecyclePort = lifecyclePort;
        this.out = out;
    }

    @Override
    public void execute() {
        controller.disconnect(() -> {
            out.print("Logout complete.");
            lifecyclePort.requestShutdown();
        });
    }
}
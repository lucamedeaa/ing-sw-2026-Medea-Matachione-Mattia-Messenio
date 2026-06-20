package it.polimi.ingsw.client.view.tui.command;

import it.polimi.ingsw.client.view.tui.ApplicationLifecyclePort;
import it.polimi.ingsw.client.view.tui.OutputPort;

/**
 * Command that disconnects from the server and shuts down the client after logout completes.
 */
public class DisconnectCommand implements GameCommand {
    private final ServerCommandPort controller;
    private final ApplicationLifecyclePort lifecyclePort;
    private final OutputPort out;

    /**
     * Creates a disconnect command.
     *
     * @param controller server command port
     * @param lifecyclePort lifecycle port used to close the application
     * @param out output port used for logout feedback
     */
    public DisconnectCommand(ServerCommandPort controller, ApplicationLifecyclePort lifecyclePort, OutputPort out) {
        this.controller = controller;
        this.lifecyclePort = lifecyclePort;
        this.out = out;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        controller.disconnect(() -> {
            out.print("Logout complete.");
            lifecyclePort.requestShutdown();
        });
    }
}

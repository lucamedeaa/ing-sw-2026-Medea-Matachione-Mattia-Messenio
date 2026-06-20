package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.view.tui.ApplicationLifecyclePort;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.render.ColorAnsi;

/**
 * Terminal state shown when the connection to the server is lost.
 */
public class DisconnectedUiState implements UIState {
    private final OutputPort out;
    private final String reason;
    private final ApplicationLifecyclePort lifecyclePort;

    /**
     * Creates a disconnected state.
     *
     * @param out output port used for rendering
     * @param reason reason to display to the user
     * @param lifecyclePort lifecycle port used to close the application
     */
    public DisconnectedUiState(OutputPort out, String reason, ApplicationLifecyclePort lifecyclePort) {
        this.out = out;
        this.reason = reason;
        this.lifecyclePort = lifecyclePort;
    }

    /** {@inheritDoc} */
    @Override
    public void render() {
        out.clearScreen();
        out.print(ColorAnsi.BG_RED_WHITE_TEXT + " FATAL ERROR " + ColorAnsi.RESET);
        out.print(ColorAnsi.RED_BOLD + "Connection to the server has been lost." + ColorAnsi.RESET);
        out.print("Reason: " + reason);
        out.print("\nPress ENTER to close the application.");
        out.prompt("> ");
    }

    /** {@inheritDoc} */
    @Override
    public void handleInput(String input) {
        lifecyclePort.requestShutdown();
    }

    /** {@inheritDoc} */
    @Override
    public void onEnter() {} // Non fa nulla, non ascolta la rete

    /** {@inheritDoc} */
    @Override
    public void onExit() {} // Non fa nulla
}

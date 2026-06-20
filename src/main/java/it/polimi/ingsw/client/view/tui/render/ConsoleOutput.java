package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;

/**
 * Console-backed implementation of the TUI output port.
 */
public class ConsoleOutput implements OutputPort {

    /** {@inheritDoc} */
    @Override
    public  void print(String msg) {
        System.out.println(msg);
    }

    /** {@inheritDoc} */
    @Override
    public  void prompt(String msg) {
        System.out.print(msg);
    }

    /** {@inheritDoc} */
    @Override
    public void clearScreen() {
        System.out.println("\n\n");
        System.out.print(ColorAnsi.CLEAR);
        System.out.flush();
    }
}

package it.polimi.ingsw.client.view.tui;

/**
 * Output abstraction used by the TUI to write text and terminal control sequences.
 */
public interface OutputPort {
    /**
     * Prints a message followed by a newline.
     *
     * @param msg message to print
     */
    void print(String msg);

    /**
     * Prints a prompt without forcing a newline.
     *
     * @param msg prompt text to print
     */
    void prompt(String msg);

    /**
     * Clears the terminal area used by the TUI.
     */
    void clearScreen();
}

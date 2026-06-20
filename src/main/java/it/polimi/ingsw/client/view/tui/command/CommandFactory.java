package it.polimi.ingsw.client.view.tui.command;

/**
 * Factory for building executable TUI commands from tokenized input.
 */
@FunctionalInterface
public interface CommandFactory {
    /**
     * Creates a command from user input arguments.
     *
     * @param args tokenized command input
     * @return executable command
     * @throws IllegalArgumentException if the arguments are not valid for the command
     */
    GameCommand create(String[] args) throws IllegalArgumentException;
}

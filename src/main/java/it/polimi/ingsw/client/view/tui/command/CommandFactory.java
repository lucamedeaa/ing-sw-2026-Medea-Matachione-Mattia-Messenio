package it.polimi.ingsw.client.view.tui.command;

@FunctionalInterface
public interface CommandFactory {
    GameCommand create(String[] args) throws IllegalArgumentException;
}
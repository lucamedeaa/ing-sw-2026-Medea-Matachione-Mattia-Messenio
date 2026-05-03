package it.polimi.ingsw.client.tui.commands;

@FunctionalInterface
public interface CommandFactory {
    GameCommand create(String[] args) throws IllegalArgumentException;
}
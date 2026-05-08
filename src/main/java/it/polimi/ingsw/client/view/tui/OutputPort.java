package it.polimi.ingsw.client.view.tui;

public interface OutputPort {
    void print(String msg);
    void prompt(String msg);
    void clearScreen();
}
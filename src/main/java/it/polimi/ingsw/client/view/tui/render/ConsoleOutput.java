package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;

public class ConsoleOutput implements OutputPort {

    @Override
    public  void print(String msg) {
        System.out.println(msg);
    }

    @Override
    public  void prompt(String msg) {
        System.out.print(msg);
    }

    @Override
    public void clearScreen() {
        System.out.println("\n\n");
        System.out.print(ColorAnsi.CLEAR);
        System.out.flush();
    }
}
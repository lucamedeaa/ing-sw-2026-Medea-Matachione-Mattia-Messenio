package it.polimi.ingsw.client.view;
import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.tui.TUI;

import java.util.Scanner;

public class UIFactory {
    public static ClientUI create(int choice, LightGameModel model, Scanner scanner) {
        return switch (choice) {
            case 1 -> new TUI(model, scanner);
            //case 2 -> new GUI(model);
            default -> throw new IllegalArgumentException("Scelta UI non valida: " + choice);
        };
    }
}
package it.polimi.ingsw.client.view;
import it.polimi.ingsw.client.lightGameModel.LobbyModel;
import it.polimi.ingsw.client.lightGameModel.MatchModel;
import it.polimi.ingsw.client.tui.TUI;

import java.util.Scanner;

public class UIFactory {
    public static ClientUI create(int choice, LobbyModel lobbyModel, MatchModel matchModel, Scanner scanner) {
        return switch (choice) {
            case 1 -> new TUI(lobbyModel, matchModel, scanner);
            //case 2 -> new GUI(model);
            default -> throw new IllegalArgumentException("Scelta UI non valida: " + choice);
        };
    }
}
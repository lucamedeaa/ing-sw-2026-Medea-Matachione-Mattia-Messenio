package it.polimi.ingsw.client.view;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiFxApp;
import it.polimi.ingsw.client.view.tui.TextUserInterface;

import java.util.Scanner;

public class UiFactory {
    public static ClientUi create(int choice, LobbyModel lobbyModel, GameModel gameModel, Scanner scanner) {
        return switch (choice) {
            case 1 -> new TextUserInterface(lobbyModel, gameModel, scanner);
            case 2 -> new GuiFxApp(lobbyModel, gameModel);
            default -> throw new IllegalArgumentException("Invalid UI selection: " + choice);
        };
    }
}
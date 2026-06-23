package it.polimi.ingsw.client.view;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiFxApp;
import it.polimi.ingsw.client.view.tui.TextUserInterface;

import java.util.Scanner;

/** Creates the selected client user interface implementation. */
public class UiFactory {
    /**
     * Creates the UI selected by the startup menu.
     *
     * @param choice selected UI type
     * @param lobbyModel lobby model
     * @param gameModel game model
     * @param scanner scanner used by the TUI
     * @return created client UI
     */
    public static ClientUi create(int choice, LobbyModel lobbyModel, GameModel gameModel, Scanner scanner) {
        return switch (choice) {
            case 1 -> new TextUserInterface(lobbyModel, gameModel, scanner);
            case 2 -> new GuiFxApp(lobbyModel, gameModel);
            default -> throw new IllegalArgumentException("Invalid UI selection: " + choice);
        };
    }
}

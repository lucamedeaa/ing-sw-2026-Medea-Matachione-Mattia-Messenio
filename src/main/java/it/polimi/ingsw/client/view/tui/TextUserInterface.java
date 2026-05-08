package it.polimi.ingsw.client.view.tui;

import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.UIObserver;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.view.tui.command.ServerCommandPort;
import it.polimi.ingsw.client.view.tui.render.ColorAnsi;
import it.polimi.ingsw.client.view.tui.state.MatchmakingUiState;
import it.polimi.ingsw.client.view.ClientUi;
import it.polimi.ingsw.client.view.tui.state.UIState;

import java.util.Scanner;

public class TextUserInterface implements ClientUi, UIObserver, NavigationPort, OutputPort {
    private UIState currentState;
    private ServerCommandPort controller;
    private ClientNotificationController notificationController;

    private final LobbyModel lobbyModel;
    private final GameModel gameModel;

    private String myNickname = "";
    private final Scanner scanner;

    public TextUserInterface(LobbyModel lobbyModel, GameModel gameModel, Scanner scanner) {
        this.lobbyModel = lobbyModel;
        this.gameModel = gameModel;
        this.scanner = scanner;

        this.lobbyModel.addObserver(this);
        this.gameModel.addObserver(this);
    }

    @Override
    public void setController(ServerController controller) {
        this.controller = controller;
    }

    @Override
    public void setNotificationController(ClientNotificationController nc) { this.notificationController = nc; }

    @Override
    public ClientNotificationController getNotificationController() { return this.notificationController; }

    @Override
    public void start() {
        // Avvio col primo stato passando i porti inietattati (che sono "this")
        changeState(new MatchmakingUiState(this, this));

        while (true) {
            String input = scanner.nextLine();
            synchronized (this) {
                if (currentState != null) {
                    currentState.handleInput(input);
                }
            }
        }
    }

    @Override
    public LobbyModel getLobbyModel() { return lobbyModel; }

    @Override
    public GameModel getMatchModel() { return gameModel; }

    @Override
    public synchronized void changeState(UIState newState) {
        this.currentState = newState;
        this.currentState.render();
    }

    @Override
    public synchronized void onStateChanged() {
        if (currentState != null) {
            currentState.render();
        }
    }

    // Implementazione NavigationPort

    @Override
    public ServerCommandPort getController() { return controller; }

    @Override
    public void setMyNickname(String nickname) { this.myNickname = nickname; }

    @Override
    public String getMyNickname() { return myNickname; }

    // Implementazione OutputPort
    @Override
    public synchronized void print(String msg) {
        System.out.println(msg);
    }

    @Override
    public synchronized void prompt(String msg) {
        System.out.print(msg);
    }

    @Override
    public synchronized void clearScreen() {
        System.out.print(ColorAnsi.CLEAR);
        System.out.flush();
    }
}
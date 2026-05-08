package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.client.lightGameModel.LobbyModel;
import it.polimi.ingsw.client.lightGameModel.MatchModel;
import it.polimi.ingsw.client.lightGameModel.UIObserver;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.states.MatchmakingState;
import it.polimi.ingsw.client.view.ClientUI;

import java.util.Scanner;

public class TUI implements ClientUI, UIObserver, NavigationPort, OutputPort {
    private UIState currentState;
    private ServerCommandPort controller;
    private ClientNotificationController notificationController;

    private final LobbyModel lobbyModel;
    private final MatchModel matchModel;

    private String myNickname = "";
    private final Scanner scanner;

    public TUI(LobbyModel lobbyModel, MatchModel matchModel, Scanner scanner) {
        this.lobbyModel = lobbyModel;
        this.matchModel = matchModel;
        this.scanner = scanner;

        this.lobbyModel.addObserver(this);
        this.matchModel.addObserver(this);
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
        changeState(new MatchmakingState(this, this));

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
    public MatchModel getMatchModel() { return matchModel; }

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
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
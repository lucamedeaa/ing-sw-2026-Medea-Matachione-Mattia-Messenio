package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.lightGameModel.UIObserver;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.tui.states.MatchmakingState;
import it.polimi.ingsw.client.view.ClientUI;

import java.util.Scanner;

public class TUI implements ClientUI, UIObserver, NavigationPort, OutputPort {
    private UIState currentState;
    private ServerController controller;
    private ClientNotificationController notificationController;
    private final LightGameModel model;
    private String myNickname = "";
    private final Scanner scanner;

    public TUI(LightGameModel model, Scanner scanner) {
        this.model = model;
        this.scanner = scanner;
        this.model.addObserver(this);
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
    public LightGameModel getModel() { return model; }

    @Override
    public ServerController getController() { return controller; }

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
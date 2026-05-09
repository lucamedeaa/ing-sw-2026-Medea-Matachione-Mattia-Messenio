package it.polimi.ingsw.client.view.tui;

import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.UIObserver;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.view.tui.command.ServerCommandPort;
import it.polimi.ingsw.client.view.tui.render.ColorAnsi;
import it.polimi.ingsw.client.view.tui.state.*;
import it.polimi.ingsw.client.view.ClientUi;

import java.util.Scanner;

public class TextUserInterface implements ClientUi, UIObserver, TuiNavigator, OutputPort {
    private UIState currentState;
    private ServerCommandPort controller;
    private ClientNotificationController notificationController;

    private final LobbyModel lobbyModel;
    private final GameModel gameModel;
    private final ClientSession session;
    private final Scanner scanner;

    public TextUserInterface(LobbyModel lobbyModel, GameModel gameModel, Scanner scanner) {
        this.lobbyModel = lobbyModel;
        this.gameModel = gameModel;
        this.scanner = scanner;
        this.session = new ClientSession();

        this.lobbyModel.addObserver(this);
        this.gameModel.addObserver(this);
    }

    @Override
    public void toMatchmaking() {
        changeState(new MatchmakingUiState(this, lobbyModel, controller, session, this, notificationController));
    }

    @Override
    public void toLobby() {
        changeState(new LobbyUiState(this, lobbyModel, controller, session, this, notificationController));
    }

    @Override
    public void toInGame() {
        changeState(new InGameUiState(this, gameModel, controller, session, this, notificationController));
    }

    @Override
    public void toViewTribe(String targetPlayer) {
        changeState(new ViewTribeUiState(this, gameModel, this, targetPlayer));
    }

    @Override
    public void toInfo() {
        changeState(new InfoUiState(this, this));
    }

    @Override
    public void toGameEnded() {
        changeState(new GameEndedUiState(this, gameModel, controller, this, notificationController));
    }

    @Override
    public void toDisconnected(String reason) {
        changeState(new DisconnectedUiState(this, reason));
    }

    @Override
    public void setController(ServerController controller) {
        this.controller = controller;
    }

    @Override
    public void setNotificationController(ClientNotificationController nc) { this.notificationController = nc; }



    @Override
    public void start() {
        // Avvio col primo stato passando i porti inietattati (che sono "this")
        toMatchmaking();

        while (true) {
            String input = scanner.nextLine();
            synchronized (this) {
                if (currentState != null) {
                    currentState.handleInput(input);
                }
            }
        }
    }


    private synchronized void changeState(UIState newState) {
        this.currentState = newState;
        this.currentState.render();
    }

    @Override
    public synchronized void onStateChanged() {
        if (currentState != null) {
            currentState.render();
        }
    }

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
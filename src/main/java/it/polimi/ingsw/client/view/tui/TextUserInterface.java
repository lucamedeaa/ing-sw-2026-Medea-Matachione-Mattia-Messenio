package it.polimi.ingsw.client.view.tui;

import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.UIObserver;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.view.tui.state.*;
import it.polimi.ingsw.client.view.ClientUi;
import it.polimi.ingsw.client.view.tui.render.ConsoleOutput;

import java.util.Scanner;

public class TextUserInterface implements ClientUi, UIObserver, StateContainer {
    private volatile UIState currentState;
    private final Scanner scanner;
    private final TuiRouter router;

    public TextUserInterface(LobbyModel lobbyModel, GameModel gameModel, Scanner scanner) {
        this.scanner = scanner;

        OutputPort out = new ConsoleOutput();
        ClientSession session = new ClientSession();


        this.router = new TuiRouter(this, lobbyModel, gameModel, session, out, null);

        lobbyModel.addObserver(this);
        gameModel.addObserver(this);
    }


    @Override
    public void setController(ServerController controller) {
        router.setController(controller);
    }

    @Override
    public void setNotificationController(ClientNotificationController nc) { router.setNotificationController(nc); }

    @Override
    public void start() {
        router.toMatchmaking();

        while (true) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                //synchronized (this) {
                    if (currentState != null) currentState.handleInput(input);
                //}
            }
        }
    }


    @Override
    public /*synchronized */void onStateChanged() {
        if (currentState != null) {
            currentState.render();
        }
    }
    @Override
    public synchronized void updateState(UIState newState) {
        this.currentState = newState;
        this.currentState.render();
    }

}
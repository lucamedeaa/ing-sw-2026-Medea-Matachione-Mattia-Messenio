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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextUserInterface implements ClientUi, UIObserver, StateContainer, ApplicationLifecyclePort {
    private volatile UIState currentState;
    private final Scanner scanner;
    private final TuiRouter router;

    private final ExecutorService uiExecutor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running = new AtomicBoolean(true);

    public TextUserInterface(LobbyModel lobbyModel, GameModel gameModel, Scanner scanner) {
        this.scanner = scanner;

        OutputPort out = new ConsoleOutput();
        ClientSession session = new ClientSession();


        this.router = new TuiRouter(this, lobbyModel, gameModel, session, out, null, this);

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
        // Avvio lo stato iniziale tramite l'executor
        uiExecutor.submit(router::toMatchmaking);

        // Il Main Thread aspetta l'input
        while (running.get()) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();

                //L'elaborazione dell'input viene accodata all'UI Executor
                uiExecutor.submit(() -> {
                    if (currentState != null && running.get()) {
                        currentState.handleInput(input);
                    }
                });
            }
        }
    }


    @Override
    public void onStateChanged() {
        // Le notifiche di rete (che arrivano da thread diversi) vengono accodate
        uiExecutor.submit(() -> {
            if (currentState != null && running.get()) {
                currentState.render();
            }
        });
    }
    @Override
    public void updateState(UIState newState) {
        //Anche i cambi di stato passano dalla coda, garantendo che
        //onExit(), onEnter() e render() non si sovrappongano a handleInput()
        uiExecutor.submit(() -> {
            if (!running.get()) return;

            if (this.currentState != null) {
                this.currentState.onExit();
            }
            this.currentState = newState;
            this.currentState.onEnter();
            this.currentState.render();
        });
    }
    @Override
    public void requestShutdown() {
        running.set(false);
        uiExecutor.shutdownNow();
        System.out.println("\nApplication closed.");
        System.exit(0); // L'unico System.exit rimasto per sbloccare lo Scanner del terminale
    }
}
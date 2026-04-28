package it.polimi.ingsw.client.tui;
import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.lightGameModel.UIObserver;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.client.view.ClientUI;
import java.util.Scanner;
import java.util.function.Consumer;

public class TUI implements ClientUI, UIObserver {
    private UIState currentState;
    private ServerController controller;
    private final LightGameModel model;
    private final Scanner scanner = new Scanner(System.in);
    public TUI(LightGameModel model) { this.model = model; }

    public ServerController getController() {
        return this.controller;
    }

    public LightGameModel getModel() {
        return model;
    }

    public synchronized void changeState(UIState newState) {
        currentState = newState;
        currentState.render();
    }

    @Override
    public void setController(ServerController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        changeState(new MatchmakingState(this));
        while (true) {
            String input = scanner.nextLine();
            synchronized (this) { currentState.handleInput(input); }
        }
    }

    @Override
     public synchronized void dispatch(Consumer<UIState> action) {
         action.accept(currentState);
     }

    @Override
    public synchronized void onStateChanged() {
        dispatch(UIState::onModelUpdated);
    }
}
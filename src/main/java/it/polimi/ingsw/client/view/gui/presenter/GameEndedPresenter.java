package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.screen.GameEndedScreen;
import it.polimi.ingsw.client.view.gui.viewstate.GameEndedViewState;
import it.polimi.ingsw.client.view.listeners.GameEndedView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

public class GameEndedPresenter implements GameEndedView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private GameEndedScreen screen;
    private volatile boolean isNavigatingAway = false;

    public GameEndedPresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    public void onScreenReady(GameEndedScreen screen) {
        this.screen = screen;
        ctx.notificationController().setGameEndedView(this);
        ctx.controller().getLeaderboard();
    }

    public void deregister() {
        ctx.notificationController().setGameEndedView(null);
    }

    public void refresh() {
        GameModel gm = ctx.gameModel();
        GameEndedViewState state = new GameEndedViewState(
                gm.getLeaderboard(),
                gm.getLocalResult(),
                gm.getGlobalLeaderboard()
        );
        Platform.runLater(() -> { if (this.screen != null) this.screen.render(state); });
    }

    // Azioni utente

    public void leave() {
        ctx.controller().leaveGame();
    }

    public void refreshLeaderboard() {
        if (screen != null) screen.showSpinner();
        ctx.controller().getLeaderboard();
    }

    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(() -> Platform.runLater(Platform::exit));
    }

    // Callbacks server (GameEndedView)

    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        Platform.runLater(() -> navigator.toMatchmaking());
    }

    @Override
    public void onServerDisconnected(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        Platform.runLater(() -> {
            navigator.toDisconnected(reason);
        });
    }
}
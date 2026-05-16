package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import it.polimi.ingsw.client.view.listeners.InGameView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

public class InGamePresenter implements InGameView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private InGameScreen screen;
    private volatile boolean isNavigatingAway = false;

    public InGamePresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    public void onScreenReady(InGameScreen screen) {
        this.screen = screen;
        ctx.notificationController().setInGameView(this);
        refresh();
    }

    public void deregister() {
        ctx.notificationController().setInGameView(null);
    }

    public void refresh() {
        if (isNavigatingAway) return;
        Platform.runLater(() -> {
            if (isNavigatingAway || screen == null) return;
            GameModel model = ctx.gameModel();
            if (model == null || model.getPlayers().isEmpty()) return;
            if (model.isGameOver()) {
                isNavigatingAway = true;
                navigator.toGameEnded();
                return;
            }
            screen.doRefresh();
        });
    }

    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(null);
        Platform.runLater(Platform::exit);
    }

    // Callbacks server (InGameView)

    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        Platform.runLater(() -> {
            navigator.toMatchmaking();
        });
    }

    @Override
    public void onError(String error) {
        Platform.runLater(() -> { if (screen != null) screen.showError(error); });
    }

    @Override
    public void onServerDisconnected(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        Platform.runLater(() -> {
            navigator.toDisconnected(reason);
        });
    }

    public void disconnect() {
        ctx.controller().disconnect(() -> Platform.runLater(() ->
                navigator.toDisconnected("Disconnected willingly")));
    }

    public void skipAction() { ctx.controller().skipAction(); }
    public void leave()      { ctx.controller().leaveGame(); }
}
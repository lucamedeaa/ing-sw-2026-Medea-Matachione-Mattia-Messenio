package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.interaction.BoardCommandPort;
import it.polimi.ingsw.client.view.gui.viewstate.*;
import it.polimi.ingsw.client.view.listeners.InGameView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

public class InGamePresenter implements InGameView, BoardCommandPort {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private InGameScreenPort screen;
    private volatile boolean isNavigatingAway = false;    // guards against refresh/navigation racing while we're already leaving the screen

    public InGamePresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    public void onScreenReady(InGameScreenPort screen) {
        this.screen = screen;
        ctx.notificationController().setInGameView(this);
        refresh();
    }

    public void deregister() {
        ctx.notificationController().setInGameView(null);
        this.screen = null;
    }

    public void refresh() {
        if (isNavigatingAway) return;
        ctx.scheduler().runLater(() -> {
            if (isNavigatingAway || screen == null) return;
            GameModel m = ctx.gameModel();
            if (m == null || m.getPlayers().isEmpty()) return;
            if (m.isGameOver()) {
                isNavigatingAway = true;
                navigator.toGameEnded();
                return;
            }
            screen.doRefresh(GameViewStateFactory.from(m, ctx.session().getNickname()));
        });
    }

    public void takeCard(int row, int col)  { ctx.controller().takeCard(row, col); }
    public void placeTotem(int position)    { ctx.controller().placeTotem(position); }
    public void skipAction()                { ctx.controller().skipAction(); }
    public void leave()                     { ctx.controller().leaveGame(); }

    public void disconnect() {
        ctx.controller().disconnect(() -> ctx.scheduler().runLater(() ->
                navigator.toDisconnected("Disconnected willingly")));
    }

    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(null);
        Platform.runLater(Platform::exit);
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(navigator::toMatchmaking);
    }

    @Override
    public void onError(String error) {
        ctx.scheduler().runLater(() -> { if (screen != null) screen.showError(error); });
    }

    @Override
    public void onServerDisconnected(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(() -> navigator.toDisconnected(reason));
    }
}

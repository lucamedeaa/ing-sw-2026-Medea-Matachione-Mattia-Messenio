package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.viewstate.GameEndedViewState;
import it.polimi.ingsw.client.view.listeners.GameEndedView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

public class GameEndedPresenter implements GameEndedView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private GameEndedScreenPort screen;
    private volatile boolean isNavigatingAway = false;

    public GameEndedPresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    public void onScreenReady(GameEndedScreenPort screen) {
        this.screen = screen;
        ctx.notificationController().setGameEndedView(this);
        refresh();
        ctx.controller().getLeaderboard();
    }

    public void deregister() {
        ctx.notificationController().setGameEndedView(null);
        this.screen = null;
    }

    public void refresh() {
        GameModel gm = ctx.gameModel();
        GameEndedViewState state = new GameEndedViewState(
                gm.getLeaderboard(),
                gm.getLocalResult(),
                gm.getGlobalLeaderboard()
        );
        ctx.scheduler().runLater(() -> { if (screen != null) screen.render(state); });
    }

    public void leave() { ctx.controller().leaveGame(); }

    public void refreshLeaderboard() {
        if (screen != null) screen.showSpinner();
        ctx.controller().getLeaderboard();
    }

    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(() -> Platform.runLater(Platform::exit));
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(navigator::toMatchmaking);
    }

    @Override
    public void onServerDisconnected(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(() -> navigator.toDisconnected(reason));
    }
}
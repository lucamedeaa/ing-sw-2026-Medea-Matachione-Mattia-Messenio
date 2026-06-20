package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.interaction.BoardCommandPort;
import it.polimi.ingsw.client.view.gui.viewstate.*;
import it.polimi.ingsw.client.view.listeners.InGameView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

/**
 * Presenter for the main in-game screen and board commands.
 */
public class InGamePresenter implements InGameView, BoardCommandPort {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private InGameScreenPort screen;
    private volatile boolean isNavigatingAway = false;    // guards against refresh/navigation racing while we're already leaving the screen

    /**
     * Creates an in-game presenter.
     *
     * @param ctx shared GUI context
     * @param navigator GUI navigator
     */
    public InGamePresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    /**
     * Registers the screen and performs the first refresh.
     *
     * @param screen screen port to update
     */
    public void onScreenReady(InGameScreenPort screen) {
        this.screen = screen;
        ctx.notificationController().setInGameView(this);
        refresh();
    }

    /**
     * Deregisters this presenter from in-game notifications.
     */
    public void deregister() {
        ctx.notificationController().setInGameView(null);
        this.screen = null;
    }

    /**
     * Rebuilds the in-game view state and renders it if navigation is stable.
     */
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

    /** {@inheritDoc} */
    public void takeCard(int row, int col)  { ctx.controller().takeCard(row, col); }

    /** {@inheritDoc} */
    public void placeTotem(int position)    { ctx.controller().placeTotem(position); }

    /**
     * Sends a skip-action command.
     */
    public void skipAction()                { ctx.controller().skipAction(); }

    /**
     * Requests leaving the current game.
     */
    public void leave()                     { ctx.controller().leaveGame(); }

    /**
     * Disconnects from the server and navigates to the disconnected screen.
     */
    public void disconnect() {
        ctx.controller().disconnect(() -> ctx.scheduler().runLater(() ->
                navigator.toDisconnected("Disconnected willingly")));
    }

    /**
     * Handles the JavaFX window close event.
     *
     * @param event close event
     */
    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(null);
        Platform.runLater(Platform::exit);
    }

    /** {@inheritDoc} */
    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(navigator::toMatchmaking);
    }

    /** {@inheritDoc} */
    @Override
    public void onError(String error) {
        ctx.scheduler().runLater(() -> { if (screen != null) screen.showError(error); });
    }

    /** {@inheritDoc} */
    @Override
    public void onServerDisconnected(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(() -> navigator.toDisconnected(reason));
    }
}

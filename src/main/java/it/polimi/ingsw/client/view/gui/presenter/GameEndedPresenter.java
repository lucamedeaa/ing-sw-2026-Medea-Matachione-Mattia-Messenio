package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.viewstate.GameEndedViewState;
import it.polimi.ingsw.client.view.listeners.GameEndedView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

/**
 * Presenter for the game-ended screen and leaderboard refresh flow.
 */
public class GameEndedPresenter implements GameEndedView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private GameEndedScreenPort screen;
    private volatile boolean isNavigatingAway = false;     // guards against double navigation when several callbacks fire while leaving`

    /**
     * Creates a game-ended presenter.
     *
     * @param ctx shared GUI context
     * @param navigator GUI navigator
     */
    public GameEndedPresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    /**
     * Registers the screen and starts the first refresh.
     *
     * @param screen screen port to update
     */
    public void onScreenReady(GameEndedScreenPort screen) {
        this.screen = screen;
        ctx.notificationController().setGameEndedView(this);
        refresh();
        ctx.controller().getLeaderboard();
    }

    /**
     * Deregisters this presenter from game-ended notifications.
     */
    public void deregister() {
        ctx.notificationController().setGameEndedView(null);
        this.screen = null;
    }

    /**
     * Rebuilds and renders the game-ended view state.
     */
    public void refresh() {
        GameModel gm = ctx.gameModel();
        GameEndedViewState state = new GameEndedViewState(
                gm.getLeaderboard(),
                gm.getLocalResult(),
                gm.getGlobalLeaderboard()
        );
        ctx.scheduler().runLater(() -> { if (screen != null) screen.render(state); });
    }

    /**
     * Requests return to matchmaking.
     */
    public void leave() { ctx.controller().leaveGame(); }

    /**
     * Requests a refreshed leaderboard and shows the loading spinner.
     */
    public void refreshLeaderboard() {
        if (screen != null) screen.showSpinner();
        ctx.controller().getLeaderboard();
    }

    /**
     * Handles the JavaFX window close event.
     *
     * @param event close event
     */
    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(() -> Platform.runLater(Platform::exit));
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
    public void onServerDisconnected(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(() -> navigator.toDisconnected(reason));
    }
}

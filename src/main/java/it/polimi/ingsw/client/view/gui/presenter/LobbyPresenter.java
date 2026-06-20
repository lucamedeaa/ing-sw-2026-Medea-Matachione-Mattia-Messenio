package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.viewstate.LobbyViewState;
import it.polimi.ingsw.client.view.listeners.LobbyView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;
import java.util.List;

/**
 * Presenter for the lobby screen.
 */
public class LobbyPresenter implements LobbyView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private LobbyScreenPort screen;
    private volatile boolean isNavigatingAway = false;     // guards against double navigation when several callbacks fire while leaving

    /**
     * Creates a lobby presenter.
     *
     * @param ctx shared GUI context
     * @param navigator GUI navigator
     */
    public LobbyPresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    /**
     * Registers the screen and renders the current lobby state.
     *
     * @param screen screen port to update
     */
    public void onScreenReady(LobbyScreenPort screen) {
        this.screen = screen;
        ctx.notificationController().setLobbyView(this);
        if (ctx.gameModel() != null && !ctx.gameModel().getPlayers().isEmpty()) {
            onGameStarted();
            return;
        }
        refresh();
    }

    /**
     * Deregisters this presenter from lobby notifications.
     */
    public void deregister() {
        ctx.notificationController().setLobbyView(null);
        this.screen = null;
    }

    /**
     * Rebuilds and renders the lobby view state.
     */
    public void refresh() {
        LobbyViewState state = new LobbyViewState(
                ctx.lobbyModel().getLobbyPlayers(),
                ctx.session().getNickname(),
                ctx.lobbyModel().getLobbyNotification()
        );
        ctx.scheduler().runLater(() -> { if (screen != null) screen.render(state); });
    }

    /**
     * Requests leaving the current lobby.
     */
    public void leave()      { ctx.controller().leaveGame(); }

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
        ctx.controller().disconnect(() -> Platform.runLater(Platform::exit));
    }

    /** {@inheritDoc} */
    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) {
        ctx.scheduler().runLater(this::refresh);
    }

    /** {@inheritDoc} */
    @Override
    public void onGameStarted() {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(navigator::toInGame);
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

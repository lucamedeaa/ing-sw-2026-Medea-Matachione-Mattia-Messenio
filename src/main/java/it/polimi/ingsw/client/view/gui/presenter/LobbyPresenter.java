package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.viewstate.LobbyViewState;
import it.polimi.ingsw.client.view.listeners.LobbyView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;
import java.util.List;

public class LobbyPresenter implements LobbyView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private LobbyScreenPort screen;
    private volatile boolean isNavigatingAway = false;     // guards against double navigation when several callbacks fire while leaving

    public LobbyPresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    public void onScreenReady(LobbyScreenPort screen) {
        this.screen = screen;
        ctx.notificationController().setLobbyView(this);
        if (ctx.gameModel() != null && !ctx.gameModel().getPlayers().isEmpty()) {
            onGameStarted();
            return;
        }
        refresh();
    }

    public void deregister() {
        ctx.notificationController().setLobbyView(null);
        this.screen = null;
    }

    public void refresh() {
        LobbyViewState state = new LobbyViewState(
                ctx.lobbyModel().getLobbyPlayers(),
                ctx.session().getNickname(),
                ctx.lobbyModel().getLobbyNotification()
        );
        ctx.scheduler().runLater(() -> { if (screen != null) screen.render(state); });
    }

    public void leave()      { ctx.controller().leaveGame(); }

    public void disconnect() {
        ctx.controller().disconnect(() -> ctx.scheduler().runLater(() ->
                navigator.toDisconnected("Disconnected willingly")));
    }

    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(() -> Platform.runLater(Platform::exit));
    }

    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) {
        ctx.scheduler().runLater(this::refresh);
    }

    @Override
    public void onGameStarted() {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(navigator::toInGame);
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
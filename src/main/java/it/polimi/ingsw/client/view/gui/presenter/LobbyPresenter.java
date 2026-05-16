package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.screen.LobbyScreen;
import it.polimi.ingsw.client.view.gui.viewstate.LobbyViewState;
import it.polimi.ingsw.client.view.listeners.LobbyView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

import java.util.List;

public class LobbyPresenter implements LobbyView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private LobbyScreen screen;

    public LobbyPresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    public void onScreenReady(LobbyScreen screen) {
        this.screen = screen;
        ctx.notificationController().setLobbyView(this);
        // Se il gameModel è già popolato (es. riconnessione), salta subito in gioco
        if (ctx.gameModel() != null && !ctx.gameModel().getPlayers().isEmpty()) {
            onGameStarted();
            return;
        }
        refresh();
    }

    public void refresh() {
        LobbyViewState state = new LobbyViewState(
                ctx.lobbyModel().getLobbyPlayers(),
                ctx.session().getNickname(),
                ctx.lobbyModel().getLobbyNotification()
        );
        Platform.runLater(() -> { if (this.screen != null) this.screen.render(state); });
    }

    // Azioni utente

    public void leave() {
        ctx.controller().leaveGame();
    }

    public void disconnect() {
        ctx.controller().disconnect(() -> Platform.runLater(() ->
                navigator.toDisconnected("Disconnected willingly")));
    }

    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(() -> Platform.runLater(Platform::exit));
    }

    //Callbacks server (LobbyView)

    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) {
        Platform.runLater(this::refresh);
    }

    @Override
    public void onGameStarted() {
        Platform.runLater(() -> {
            ctx.notificationController().setLobbyView(null);
            navigator.toInGame();
        });
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        Platform.runLater(() -> {
            ctx.notificationController().setLobbyView(null);
            navigator.toMatchmaking();
        });
    }

    @Override
    public void onError(String error) {
        Platform.runLater(() -> { if (screen != null) screen.showError(error); });
    }

    @Override
    public void onServerDisconnected(String reason) {
        Platform.runLater(() -> {
            ctx.notificationController().setLobbyView(null);
            navigator.toDisconnected(reason);
        });
    }
}
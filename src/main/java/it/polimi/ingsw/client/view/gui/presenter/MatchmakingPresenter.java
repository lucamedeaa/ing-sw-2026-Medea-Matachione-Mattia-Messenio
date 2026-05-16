package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.screen.MatchmakingScreen;
import it.polimi.ingsw.client.view.gui.viewstate.MatchmakingViewState;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

import java.util.List;

public class MatchmakingPresenter implements MatchmakingView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private MatchmakingScreen screen;
    private String pendingNickname = "";

    public MatchmakingPresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    /** Chiamato da MatchmakingScreen.initialize() dopo il binding FXML. */
    public void onScreenReady(MatchmakingScreen screen) {
        this.screen = screen;
        ctx.notificationController().setMatchmakingView(this);
        refresh();
    }

    public void refresh() {
        List<GameInfoDto> games = ctx.lobbyModel().getAvailableGames();
        String error = ctx.lobbyModel().consumeGlobalError();
        MatchmakingViewState state = new MatchmakingViewState(games, error);
        Platform.runLater(() -> { if (this.screen != null) this.screen.render(state); });
    }

    // Azioni utente (delegate dalla screen)

    public void createGame(String nickname, int maxPlayers) {
        this.pendingNickname = nickname;
        ctx.controller().createGame(nickname, maxPlayers);
    }

    public void joinGame(String nickname, GameInfoDto selected) {
        if (selected == null) return;
        this.pendingNickname = nickname;
        ctx.controller().joinGame(nickname, selected.getGameId());
    }

    public void refreshList() {
        ctx.controller().getAvailableGames();
    }

    public void disconnect() {
        ctx.controller().disconnect(() -> Platform.runLater(() -> {
            ctx.notificationController().setMatchmakingView(null);
            navigator.toDisconnected("Disconnected willingly");
        }));
    }

    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(() -> Platform.runLater(Platform::exit));
    }

    // Callbacks server (MatchmakingView)

    @Override
    public void onAvailableGames(List<GameInfoDto> games) {
        Platform.runLater(this::refresh);
    }

    @Override
    public void onMatchmakingSuccess(String text) {
        Platform.runLater(() -> {
            ctx.notificationController().setMatchmakingView(null);
            ctx.session().setNickname(pendingNickname);
            navigator.toLobby();
        });
    }

    @Override
    public void onError(String error) {
        Platform.runLater(this::refresh);
    }

    @Override
    public void onServerDisconnected(String reason) {
        Platform.runLater(() -> {
            ctx.notificationController().setMatchmakingView(null);
            navigator.toDisconnected(reason);
        });
    }
}
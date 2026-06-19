package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.viewstate.MatchmakingViewState;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import javafx.application.Platform;
import javafx.stage.WindowEvent;
import java.util.List;

public class MatchmakingPresenter implements MatchmakingView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private MatchmakingScreenPort screen;
    private String pendingNickname = "";   // kept until the server confirms matchmaking, then committed to the session

    public MatchmakingPresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    public void onScreenReady(MatchmakingScreenPort screen) {
        this.screen = screen;
        ctx.notificationController().setMatchmakingView(this);
        refresh();
    }

    public void deregister() {
        ctx.notificationController().setMatchmakingView(null);
        this.screen = null;
    }

    public void refresh() {
        List<GameInfoDto> games = ctx.lobbyModel().getAvailableGames();
        String error = ctx.lobbyModel().consumeGlobalError();
        MatchmakingViewState state = new MatchmakingViewState(games, error);
        ctx.scheduler().runLater(() -> { if (screen != null) screen.render(state); });
    }

    public void createGame(String nickname, int maxPlayers) {
        this.pendingNickname = nickname;
        ctx.controller().createGame(nickname, maxPlayers);
    }

    public void joinGame(String nickname, GameInfoDto selected) {
        if (selected == null) return;
        this.pendingNickname = nickname;
        ctx.controller().joinGame(nickname, selected.getGameId());
    }

    public void refreshList() { ctx.controller().getAvailableGames(); }

    public void disconnect() {
        ctx.controller().disconnect(() -> ctx.scheduler().runLater(() ->
                navigator.toDisconnected("Disconnected willingly")));
    }

    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(() -> Platform.runLater(Platform::exit));
    }

    @Override public void onAvailableGames(List<GameInfoDto> games) { ctx.scheduler().runLater(this::refresh); }
    @Override
    public void onError(String error) {
        ctx.scheduler().runLater(() -> {
            if (screen != null) {
                MatchmakingViewState state = new MatchmakingViewState(ctx.lobbyModel().getAvailableGames(), error);
                screen.render(state);
            }
        });
    }

    @Override
    public void onMatchmakingSuccess(String text) {
        ctx.scheduler().runLater(() -> {
            ctx.session().setNickname(pendingNickname);
            navigator.toLobby();
        });
    }

    @Override
    public void onServerDisconnected(String reason) {
        ctx.scheduler().runLater(() -> navigator.toDisconnected(reason));
    }
}
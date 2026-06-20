package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.viewstate.MatchmakingViewState;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import javafx.application.Platform;
import javafx.stage.WindowEvent;
import java.util.List;

/**
 * Presenter for the matchmaking screen.
 */
public class MatchmakingPresenter implements MatchmakingView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private MatchmakingScreenPort screen;
    private String pendingNickname = "";   // kept until the server confirms matchmaking, then committed to the session

    /**
     * Creates a matchmaking presenter.
     *
     * @param ctx shared GUI context
     * @param navigator GUI navigator
     */
    public MatchmakingPresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    /**
     * Registers the screen and renders the current matchmaking state.
     *
     * @param screen screen port to update
     */
    public void onScreenReady(MatchmakingScreenPort screen) {
        this.screen = screen;
        ctx.notificationController().setMatchmakingView(this);
        refresh();
    }

    /**
     * Deregisters this presenter from matchmaking notifications.
     */
    public void deregister() {
        ctx.notificationController().setMatchmakingView(null);
        this.screen = null;
    }

    /**
     * Rebuilds and renders the matchmaking view state.
     */
    public void refresh() {
        List<GameInfoDto> games = ctx.lobbyModel().getAvailableGames();
        String error = ctx.lobbyModel().consumeGlobalError();
        MatchmakingViewState state = new MatchmakingViewState(games, error);
        ctx.scheduler().runLater(() -> { if (screen != null) screen.render(state); });
    }

    /**
     * Requests creation of a new game.
     *
     * @param nickname nickname chosen by the player
     * @param maxPlayers maximum number of players
     */
    public void createGame(String nickname, int maxPlayers) {
        this.pendingNickname = nickname;
        ctx.controller().createGame(nickname, maxPlayers);
    }

    /**
     * Requests joining an existing game.
     *
     * @param nickname nickname chosen by the player
     * @param selected selected game info
     */
    public void joinGame(String nickname, GameInfoDto selected) {
        if (selected == null) return;
        this.pendingNickname = nickname;
        ctx.controller().joinGame(nickname, selected.getGameId());
    }

    /**
     * Requests the available-games list.
     */
    public void refreshList() { ctx.controller().getAvailableGames(); }

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
    @Override public void onAvailableGames(List<GameInfoDto> games) { ctx.scheduler().runLater(this::refresh); }

    /** {@inheritDoc} */
    @Override
    public void onError(String error) {
        ctx.scheduler().runLater(() -> {
            if (screen != null) {
                MatchmakingViewState state = new MatchmakingViewState(ctx.lobbyModel().getAvailableGames(), error);
                screen.render(state);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onMatchmakingSuccess(String text) {
        ctx.scheduler().runLater(() -> {
            ctx.session().setNickname(pendingNickname);
            navigator.toLobby();
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onServerDisconnected(String reason) {
        ctx.scheduler().runLater(() -> navigator.toDisconnected(reason));
    }
}

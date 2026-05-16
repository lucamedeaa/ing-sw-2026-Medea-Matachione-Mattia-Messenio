package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.controllers.board.CardAffordabilityPolicy;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import it.polimi.ingsw.client.view.gui.viewstate.ActionsViewState;
import it.polimi.ingsw.client.view.gui.viewstate.BoardViewState;
import it.polimi.ingsw.client.view.gui.viewstate.GameViewState;
import it.polimi.ingsw.client.view.gui.viewstate.PlayerInfo;
import it.polimi.ingsw.client.view.listeners.InGameView;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class InGamePresenter implements InGameView {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private InGameScreen screen;
    private volatile boolean isNavigatingAway = false;

    public InGamePresenter(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    public void onScreenReady(InGameScreen screen) {
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

            String self = ctx.session().getNickname();

            List<PlayerInfo> playerInfos = m.getPlayers().values().stream()
                    .map(p -> new PlayerInfo(p.getNickname(), p.getTotemColor(), p.getFood(), p.getPrestige()))
                    .toList();

            PlayerSnapshot me = m.getPlayers().get(self);
            Set<Integer> affordable   = new HashSet<>();
            Set<Integer> unaffordable = new HashSet<>();
            if (me != null) {
                Stream.concat(m.getUpperRowCards().stream(), m.getLowerRowCards().stream())
                        .filter(id -> id != null && !CardAffordabilityPolicy.isEvent(id))
                        .forEach(id -> {
                            if (CardAffordabilityPolicy.isAffordable(id, me)) affordable.add(id);
                            else unaffordable.add(id);
                        });
            }

            GameViewState state = new GameViewState(
                    new BoardViewState(
                            m.getPlayers().size(),
                            m.getUpperRowCards(),
                            m.getLowerRowCards(),
                            playerInfos,
                            m.getTotemPositions(),
                            m.getReturnPositions()),
                    new ActionsViewState(
                            m.getMyActions(),
                            self.equals(m.getActivePlayer()),
                            affordable,
                            unaffordable),
                    playerInfos,
                    m.getTribes(),
                    m.consumeGameLogs(),
                    self,
                    m.getActivePlayer()
            );

            screen.doRefresh(state);
        });
    }

    public void disconnect() {
        ctx.controller().disconnect(() -> ctx.scheduler().runLater(() ->
                navigator.toDisconnected("Disconnected willingly")));
    }

    public void skipAction() { ctx.controller().skipAction(); }

    public void leave() { ctx.controller().leaveGame(); }

    public void handleWindowClose(WindowEvent event) {
        ctx.controller().disconnect(null);
        Platform.runLater(Platform::exit);
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.scheduler().runLater(() -> navigator.toMatchmaking());
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
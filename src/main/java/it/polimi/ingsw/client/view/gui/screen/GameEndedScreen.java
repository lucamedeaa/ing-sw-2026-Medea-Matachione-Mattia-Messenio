package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.listeners.GameEndedView;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.PlayerScoreDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;

import java.util.List;

public class GameEndedScreen implements GameEndedView, RefreshableScreen {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private boolean isNavigatingAway = false;

    @FXML private Label positionLabel;
    @FXML private Label scoreLabel;
    @FXML private Label foodLabel;
    @FXML private Label personalBestLabel;
    @FXML private ListView<String> sessionLeaderboardView;
    @FXML private ProgressIndicator globalSpinner;
    @FXML private ListView<String> globalLeaderboardView;

    public GameEndedScreen(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        ctx.notificationController().setGameEndedView(this);
        // Richiede la leaderboard globale; quando arriva, il server aggiorna GameModel
        // → notifyUI() → GuiFxRouter.onStateChanged() → refresh()
        ctx.controller().getLeaderboard();
    }

    @Override
    public void refresh() {
        // Già sul thread JavaFX — chiamato da GuiFxRouter via Platform.runLater
        renderLocalResult();
        renderGlobalLeaderboard();
    }

    private void renderLocalResult() {
        GameModel gm = ctx.gameModel();
        gm.getReadLock().lock();
        try {
            List<PlayerScoreDto> scores = gm.getLeaderboard();
            if (scores != null && !scores.isEmpty()) {
                sessionLeaderboardView.getItems().setAll(
                        scores.stream()
                                .map(s -> s.nickname() + "  —  " + s.finalScore() + " pt  (" + s.remainingFood() + " food)")
                                .toList()
                );
            }

            PlayerGameCompletedDto local = gm.getLocalResult();
            if (local != null) {
                positionLabel.setText("Posizione: " + local.localPosition() + " / " + local.playerCount());
                scoreLabel.setText("Punteggio: " + local.localScore());
                foodLabel.setText("Cibo rimanente: " + local.localRemainingFood());
                personalBestLabel.setText("Miglior punteggio globale: "
                        + local.personalBestScore() + " pt  (pos. " + local.globalPersonalBestPosition() + ")");
            }
        } finally {
            gm.getReadLock().unlock();
        }
    }

    private void renderGlobalLeaderboard() {
        GameModel gm = ctx.gameModel();
        gm.getReadLock().lock();
        LeaderboardSnapshotDto global;
        try {
            global = gm.getGlobalLeaderboard();
        } finally {
            gm.getReadLock().unlock();
        }

        if (global == null) {
            globalSpinner.setVisible(true);
            globalLeaderboardView.setVisible(false);
        } else {
            globalSpinner.setVisible(false);
            globalLeaderboardView.setVisible(true);
            globalLeaderboardView.getItems().setAll(
                    global.entries().stream()
                            .map(e -> e.position() + ". " + e.nickname()
                                    + "  —  " + e.finalScore() + " pt  (" + e.remainingFood() + " food)")
                            .toList()
            );
        }
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.notificationController().setGameEndedView(null);
        Platform.runLater(navigator::toMatchmaking);
    }

    @Override
    public void onServerDisconnected(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.notificationController().setGameEndedView(null);
        Platform.runLater(() -> navigator.toDisconnected(reason));
    }

    @FXML
    private void handleLeave() {
        ctx.controller().leaveGame();
    }
}

    //Controller FXML della schermata fine partita. Implementa GameEndedView e UIObserver.
    // Non chiama mai addObserver. In initialize() chiama controller.getLeaderboard();
    // quando il server risponde, ClientNotificationController aggiorna GameModel → notifyUI() → router → onStateChanged()
    // già sul thread JavaFX → legge il leaderboard e popola la tabella. Mostra spinner finché il dato non arriva.
    //GameEndedScreen deve renderizzare subito il risultato locale anche se la leaderboard globale non è ancora arrivata.


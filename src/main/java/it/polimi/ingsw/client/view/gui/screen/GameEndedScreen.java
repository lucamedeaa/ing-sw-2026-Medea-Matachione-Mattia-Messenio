package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.gui.media.VideoBackground;
import it.polimi.ingsw.client.view.listeners.GameEndedView;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.PlayerScoreDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.time.format.DateTimeFormatter;

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

    @FXML private StackPane videoContainer;
    @FXML private Slider volumeSlider;

    private final VideoBackground videoBackground = new VideoBackground();

    public GameEndedScreen(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        ctx.notificationController().setGameEndedView(this);

        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.5);
        videoBackground.start(videoContainer, "/background/VideoMesosBG.mp4", volumeSlider.valueProperty());

        setupListViewStyle(sessionLeaderboardView);
        setupListViewStyle(globalLeaderboardView);

        ctx.controller().getLeaderboard();
    }



    private void setupListViewStyle(ListView<String> listView) {
        listView.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-background-insets: 0;");
        listView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: transparent; -fx-text-fill: #fdf5e6; -fx-font-family: 'MedievalSharp', serif; -fx-font-size: 18px; -fx-effect: dropshadow(three-pass-box, black, 5, 0.0, 0, 2);");
                }
            }
        });
    }

    @Override
    public void refresh() {
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

            // Creiamo un formatter per rendere la data leggibile
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            globalLeaderboardView.getItems().setAll(
                    global.entries().stream()
                            .map(e -> String.format("%d. %-12s —  %d pt  (%d food)   |   %s",
                                    e.position(),
                                    e.nickname(),
                                    e.finalScore(),
                                    e.remainingFood(),
                                    e.playedAt().format(formatter)))
                            .toList()
            );
        }
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        videoBackground.stop();
        ctx.notificationController().setGameEndedView(null);
        Platform.runLater(navigator::toMatchmaking);
    }
    @FXML
    private void handleRefresh() {
        globalSpinner.setVisible(true);
        globalLeaderboardView.setVisible(false);
        // Richiede nuovamente la classifica al server
        ctx.controller().getLeaderboard();
    }
    @Override
    public void onServerDisconnected(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        videoBackground.stop();
        ctx.notificationController().setGameEndedView(null);
        Platform.runLater(() -> navigator.toDisconnected(reason));
    }

    @FXML
    private void handleLeave() {
        videoBackground.stop();
        ctx.controller().leaveGame();
    }
}
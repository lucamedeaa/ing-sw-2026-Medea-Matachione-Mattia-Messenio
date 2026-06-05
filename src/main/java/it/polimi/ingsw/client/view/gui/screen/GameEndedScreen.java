package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiAssetPaths;
import it.polimi.ingsw.client.view.gui.media.VideoBackground;
import it.polimi.ingsw.client.view.gui.presenter.GameEndedPresenter;
import it.polimi.ingsw.client.view.gui.presenter.GameEndedScreenPort;
import it.polimi.ingsw.client.view.gui.viewstate.GameEndedViewState;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerScoreDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.time.format.DateTimeFormatter;

public class GameEndedScreen implements RefreshableScreen, GameEndedScreenPort {

    private final GameEndedPresenter presenter;

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

    public GameEndedScreen(GameEndedPresenter presenter) {
        this.presenter = presenter;
    }

    @FXML
    public void initialize() {
        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(VideoBackground.getGlobalVolume());

        videoBackground.start(videoContainer, GuiAssetPaths.VIDEO_BG, volumeSlider.valueProperty());
        Platform.runLater(() -> {
            if (videoContainer != null && videoContainer.getScene() != null) {
                Stage stage = (Stage) videoContainer.getScene().getWindow();
                if (stage != null) { stage.setMinWidth(1280); stage.setMinHeight(720); }
            }
        });
        setupListViewStyle(sessionLeaderboardView);
        setupListViewStyle(globalLeaderboardView);
    }

    private void setupListViewStyle(ListView<String> lv) {
        lv.getStyleClass().add("result-list");
        lv.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().remove("result-list-cell");
                } else {
                    setText(item);
                    if (!getStyleClass().contains("result-list-cell"))
                        getStyleClass().add("result-list-cell");
                }
            }
        });
    }

    public void render(GameEndedViewState state) {
        renderSessionScores(state.sessionScores());
        renderLocalResult(state);
        renderGlobalLeaderboard(state.globalLeaderboard());
    }

    private void renderSessionScores(java.util.List<PlayerScoreDto> scores) {
        if (scores != null && !scores.isEmpty()) {
            sessionLeaderboardView.getItems().setAll(
                    scores.stream()
                            .map(s -> s.nickname() + "  —  " + s.finalScore() + " pt  (" + s.remainingFood() + " food)")
                            .toList());
        }
    }

    private void renderLocalResult(GameEndedViewState state) {
        var local = state.localResult();
        if (local != null) {
            positionLabel.setText("Posizione: " + local.localPosition() + " / " + local.playerCount());
            scoreLabel.setText("Punteggio: " + local.localScore());
            foodLabel.setText("Cibo rimanente: " + local.localRemainingFood());
            personalBestLabel.setText("Miglior punteggio globale: "
                    + local.personalBestScore() + " pt  (pos. " + local.globalPersonalBestPosition() + ")");
        }
    }

    private void renderGlobalLeaderboard(LeaderboardSnapshotDto global) {
        if (global == null) {
            globalSpinner.setVisible(true);
            globalLeaderboardView.setVisible(false);
        } else {
            globalSpinner.setVisible(false);
            globalLeaderboardView.setVisible(true);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            globalLeaderboardView.getItems().setAll(
                    global.entries().stream()
                            .map(e -> String.format("%d. %-12s —  %d pt  (%d food)   |   %s",
                                    e.position(), e.nickname(), e.finalScore(),
                                    e.remainingFood(), e.playedAt().format(fmt)))
                            .toList());
        }
    }

    public void showSpinner() {
        globalSpinner.setVisible(true);
        globalLeaderboardView.setVisible(false);
    }

    @Override
    public void onEnter() { presenter.onScreenReady(this); }

    @Override
    public void onExit() {
        presenter.deregister();
        videoBackground.stop();
    }

    @Override
    public void refresh() { presenter.refresh(); }

    @Override
    public void handleWindowClose(WindowEvent event) {
        presenter.handleWindowClose(event);
    }

    @FXML private void handleLeave()    { presenter.leave(); }
    @FXML private void handleRefresh()  { presenter.refreshLeaderboard(); }
}
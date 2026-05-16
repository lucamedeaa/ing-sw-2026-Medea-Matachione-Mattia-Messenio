package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiAssetPaths;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.media.VideoBackground;
import it.polimi.ingsw.client.view.gui.presenter.LobbyPresenter;
import it.polimi.ingsw.client.view.gui.viewstate.LobbyViewState;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

public class LobbyScreen implements RefreshableScreen {

    private final LobbyPresenter presenter;

    @FXML private VBox playersContainer;
    @FXML private Label statusLabel;
    @FXML private StackPane videoContainer;
    @FXML private Slider volumeSlider;

    private final VideoBackground videoBackground = new VideoBackground();

    public LobbyScreen(LobbyPresenter presenter) {
        this.presenter = presenter;
    }

    @FXML
    public void initialize() {
        playersContainer.setAlignment(Pos.CENTER);
        playersContainer.setSpacing(20);
        statusLabel.getStyleClass().add("lobby-status");
        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(VideoBackground.getGlobalVolume());
        videoBackground.start(videoContainer, GuiAssetPaths.VIDEO_BG, volumeSlider.valueProperty());
        Platform.runLater(() -> {
            if (playersContainer != null && playersContainer.getScene() != null) {
                Stage stage = (Stage) playersContainer.getScene().getWindow();
                if (stage != null) { stage.setMinWidth(1280); stage.setMinHeight(720); }
            }
        });
    }

    public void render(LobbyViewState state) {
        playersContainer.getChildren().clear();

        String base = "-fx-font-family: 'MedievalSharp'; -fx-font-size: 54px; -fx-font-weight: bold; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.9), 20, 0.4, 0, 8);";

        for (String player : state.players()) {
            Label lbl = new Label(player.toUpperCase());
            lbl.setStyle(base + (player.equals(state.selfNickname())
                    ? "-fx-text-fill: #e67e22;"
                    : "-fx-text-fill: #fdf5e6;"));
            playersContainer.getChildren().add(lbl);
        }

        if (!state.notification().isEmpty()) {
            statusLabel.setText(state.notification().toUpperCase());
            statusLabel.setVisible(true);
        } else {
            statusLabel.setVisible(false);
        }
    }

    public void showError(String error) {
        statusLabel.setText(error);
        statusLabel.setVisible(true);
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
    public void handleWindowClose(WindowEvent event, GuiContext ctx, GuiNavigator navigator) {
        presenter.handleWindowClose(event);
    }


    @FXML private void handleLeave()      { presenter.leave(); }
    @FXML private void handleDisconnect() { presenter.disconnect(); }
}
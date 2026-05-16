package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.gui.media.VideoBackground;
import it.polimi.ingsw.client.view.listeners.LobbyView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

import java.util.List;


public class LobbyScreen implements LobbyView, RefreshableScreen {

    private final GuiContext ctx;
    private final GuiNavigator navigator;


    @FXML private VBox playersContainer;
    @FXML private Label statusLabel;


    @FXML private StackPane videoContainer;
    @FXML private Slider volumeSlider;


    private final VideoBackground videoBackground = new VideoBackground();



    // Nuovo costruttore per la Factory
    public LobbyScreen(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        ctx.notificationController().setLobbyView(this);

        if (ctx.gameModel() != null && !ctx.gameModel().getPlayers().isEmpty()) {
            Platform.runLater(this::onGameStarted);
            return;
        }

        Platform.runLater(this::refresh);

        Platform.runLater(() -> {
            if (playersContainer != null && playersContainer.getScene() != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) playersContainer.getScene().getWindow();
                stage.setMinWidth(1280);
                stage.setMinHeight(720);
            }
        });

        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(VideoBackground.getGlobalVolume());

        // Spaziatura e allineamento centrale per i nomi dei giocatori
        playersContainer.setAlignment(Pos.CENTER);
        playersContainer.setSpacing(20);


        statusLabel.setStyle("-fx-font-family: 'MedievalSharp'; -fx-font-size: 22px; " +
                "-fx-text-fill: #ffd700; -fx-background-color: rgba(0,0,0,0.5); " +
                "-fx-padding: 15px 30px; -fx-background-radius: 20px; " +
                "-fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0);");



        startVideoBackground();
    }

    private void startVideoBackground() {
        videoBackground.start(videoContainer, "/backGround/VideoMesosBG.mp4", volumeSlider.valueProperty());
    }

    private void stopVideo() {
        videoBackground.stop();
    }

    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) {
        // Ignoriamo i parametri, ma dobbiamo forzare la UI a rileggere il LobbyModel
        Platform.runLater(this::refresh);
    }

    @Override
    public void onGameStarted() {
        Platform.runLater(() -> {
            stopVideo();
            ctx.notificationController().setLobbyView(null);
            navigator.toInGame();
        });
    }

    @FXML
    private void handleDisconnect() {
        stopVideo();
        ctx.controller().disconnect(() -> {
            Platform.runLater(() -> navigator.toDisconnected("Disconnected willingly"));
        });
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        stopVideo();
        ctx.notificationController().setLobbyView(null);
        Platform.runLater(navigator::toMatchmaking);
    }

    @Override
    public void onError(String error) {
        // Aggiorna direttamente l'UI, quindi serve Platform.runLater
        Platform.runLater(() -> statusLabel.setText(error));
    }

    @Override
    public void onServerDisconnected(String reason) {
        Platform.runLater(() -> {
            stopVideo();
            ctx.notificationController().setLobbyView(null);
            navigator.toDisconnected(reason);
        });
    }

    @Override
    public void refresh() {

            playersContainer.getChildren().clear();
            List<String> players = ctx.lobbyModel().getLobbyPlayers();

            String baseNameStyle = "-fx-font-family: 'MedievalSharp'; -fx-font-size: 54px; -fx-font-weight: bold; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.9), 20, 0.4, 0, 8);";

            for (String player : players) {
                Label playerLabel = new Label(player.toUpperCase());

                if (player.equals(ctx.session().getNickname())) {
                    // TU (Oro acceso)
                    playerLabel.setStyle(baseNameStyle + "-fx-text-fill: #e67e22;");
                } else {
                    // ALTRI (Bianco Avorio)
                    playerLabel.setStyle(baseNameStyle + "-fx-text-fill: #fdf5e6;");
                }
                playersContainer.getChildren().add(playerLabel);
            }

            String notification = ctx.lobbyModel().getLobbyNotification();
            if (notification != null && !notification.isEmpty()) {
                statusLabel.setText(notification.toUpperCase());
                statusLabel.setVisible(true);
            } else {
                statusLabel.setVisible(false);
            }

    }

    @FXML
    private void handleLeave() {
        stopVideo();
        ctx.controller().leaveGame();
    }
}

package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.gui.media.VideoBackground;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.util.List;

public class MatchmakingScreen implements MatchmakingView, RefreshableScreen {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private boolean showGamesList = false;
    private String pendingNickname = "";

    @FXML private TextField nicknameField;
    @FXML private ComboBox<Integer> maxPlayersComboBox;
    @FXML private ListView<String> gamesListView;
    @FXML private Label errorLabel;

    @FXML private Button createGameButton;
    @FXML private Button joinGameButton;

    @FXML private StackPane videoContainer;
    @FXML private Slider volumeSlider;

    private final VideoBackground videoBackground = new VideoBackground();

    public MatchmakingScreen(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        ctx.notificationController().setMatchmakingView(this);

        maxPlayersComboBox.getItems().addAll(2, 3, 4, 5);

        // Gestione dimensione minima per non far collassare la UI
        Platform.runLater(() -> {
            if (videoContainer != null && videoContainer.getScene() != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) videoContainer.getScene().getWindow();
                if (stage != null) {
                    stage.setMinWidth(1280);
                    stage.setMinHeight(720);
                }
            }
        });

        // Stili UI
        String buttonStyle = "-fx-font-family: 'MedievalSharp', serif; -fx-background-color: rgba(15, 15, 15, 0.9); -fx-text-fill: #e67e22; -fx-font-size: 22px; -fx-border-color: #e67e22; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.0, 0, 4);";
        String fieldStyle = "-fx-font-family: 'MedievalSharp', serif; -fx-background-color: rgba(0, 0, 0, 0.7); -fx-text-fill: white; -fx-font-size: 18px; -fx-border-color: #777; -fx-border-radius: 4; -fx-background-radius: 4; -fx-prompt-text-fill: #aaaaaa;";

        createGameButton.setStyle(buttonStyle);
        joinGameButton.setStyle(buttonStyle);
        nicknameField.setStyle(fieldStyle);
        maxPlayersComboBox.setStyle(fieldStyle);
        maxPlayersComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    // Forza il testo bianco e lo sfondo trasparente per allinearsi al TextField
                    setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-font-family: 'MedievalSharp', serif; -fx-font-size: 18px;");
                }
            }
        });

        errorLabel.setStyle("-fx-font-family: 'MedievalSharp', serif; -fx-font-size: 18px; -fx-text-fill: #ff5252; -fx-effect: dropshadow(three-pass-box, black, 5, 0.0, 0, 2);");

        gamesListView.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-background-insets: 0;");

        gamesListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: transparent; -fx-text-fill: #fdf5e6; -fx-font-family: 'MedievalSharp', serif; -fx-font-size: 20px; -fx-effect: dropshadow(three-pass-box, black, 8, 0.0, 0, 2);");
                }
            }
        });

        createGameButton.disableProperty().bind(
                nicknameField.textProperty().isEmpty()
                        .or(maxPlayersComboBox.valueProperty().isNull())
        );

        joinGameButton.disableProperty().bind(
                nicknameField.textProperty().isEmpty()
                        .or(gamesListView.getSelectionModel().selectedItemProperty().isNull())
        );

        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.5);

        startVideoBackground();
    }

    private void startVideoBackground() {
        videoBackground.start(videoContainer, "/backGround/VideoMesosBG.mp4", volumeSlider.valueProperty());
    }

    private void stopVideo() {
        videoBackground.stop();
    }

    @Override
    public void refresh() {
        String error = ctx.lobbyModel().consumeGlobalError();
        errorLabel.setText(error != null ? error : "");

        if (showGamesList) {
            ctx.lobbyModel().getReadLock().lock();
            try {
                List<GameInfoDto> games = ctx.lobbyModel().getAvailableGames();
                gamesListView.getItems().setAll(
                        games.stream()
                                .map(g -> g.getGameId() + " — Creator: " + g.getCreatorNickname() + " (" + g.getCurrentPlayers() + "/" + g.getMaxPlayers() + ")")
                                .toList()
                );
            } finally {
                ctx.lobbyModel().getReadLock().unlock();
            }
        }
    }

    @Override
    public void onAvailableGames(List<GameInfoDto> games) {
        Platform.runLater(() -> {
            showGamesList = true;
            refresh();
        });
    }

    @Override
    public void onMatchmakingSuccess(String text) {
        Platform.runLater(() -> {
            stopVideo();
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
            stopVideo();
            ctx.notificationController().setMatchmakingView(null);
            navigator.toLobby();
        });
    }

    @FXML
    private void onCreateGame() {
        pendingNickname = nicknameField.getText().trim();
        int maxPlayers = maxPlayersComboBox.getValue();
        ctx.controller().createGame(pendingNickname, maxPlayers);
    }

    @FXML
    private void onJoinGame() {
        pendingNickname = nicknameField.getText().trim();
        String selected = gamesListView.getSelectionModel().getSelectedItem();
        String gameId = selected.split(" — ")[0];
        ctx.controller().joinGame(pendingNickname, gameId);
    }

    @FXML
    private void onRefreshList() {
        ctx.controller().getAvailableGames();
    }
}

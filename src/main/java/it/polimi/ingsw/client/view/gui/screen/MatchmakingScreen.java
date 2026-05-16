package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiAssetPaths;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.gui.media.VideoBackground;
import it.polimi.ingsw.client.view.gui.presenter.MatchmakingPresenter;
import it.polimi.ingsw.client.view.gui.viewstate.MatchmakingViewState;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MatchmakingScreen implements RefreshableScreen {

    private final MatchmakingPresenter presenter;

    @FXML private TextField nicknameField;
    @FXML private ComboBox<Integer> maxPlayersComboBox;
    @FXML private ListView<GameInfoDto> gamesListView;   // GameInfoDto, non più String
    @FXML private Label errorLabel;
    @FXML private Button createGameButton;
    @FXML private Button joinGameButton;
    @FXML private StackPane videoContainer;
    @FXML private Slider volumeSlider;

    private final VideoBackground videoBackground = new VideoBackground();

    public MatchmakingScreen(MatchmakingPresenter presenter) {
        this.presenter = presenter;
    }

    @FXML
    public void initialize() {
        maxPlayersComboBox.getItems().addAll(2, 3, 4, 5);
        setupStyles();
        setupBindings();
        setupVideoAndStage();
        presenter.onScreenReady(this);
    }

    private void setupStyles() {
        String buttonStyle = "-fx-font-family: 'MedievalSharp', serif; -fx-background-color: rgba(15,15,15,0.9); -fx-text-fill: #e67e22; -fx-font-size: 22px; -fx-border-color: #e67e22; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.0, 0, 4);";
        String fieldStyle  = "-fx-font-family: 'MedievalSharp', serif; -fx-background-color: rgba(0,0,0,0.7); -fx-text-fill: white; -fx-font-size: 18px; -fx-border-color: #777; -fx-border-radius: 4; -fx-background-radius: 4; -fx-prompt-text-fill: #aaaaaa;";

        createGameButton.setStyle(buttonStyle);
        joinGameButton.setStyle(buttonStyle);
        nicknameField.setStyle(fieldStyle);
        maxPlayersComboBox.setStyle(fieldStyle);
        maxPlayersComboBox.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText((!empty && item != null) ? item.toString() : null);
                setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-font-family: 'MedievalSharp', serif; -fx-font-size: 18px;");
            }
        });
        errorLabel.setStyle("-fx-font-family: 'MedievalSharp', serif; -fx-font-size: 18px; -fx-text-fill: #ff5252; -fx-effect: dropshadow(three-pass-box, black, 5, 0.0, 0, 2);");
        gamesListView.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-background-insets: 0;");
        gamesListView.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(GameInfoDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle("-fx-background-color: transparent;"); return; }
                setText(item.getGameId() + " — Creator: " + item.getCreatorNickname()
                        + " (" + item.getCurrentPlayers() + "/" + item.getMaxPlayers() + ")");
                applyStyle(isSelected());
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                if (!isEmpty() && getItem() != null) applyStyle(selected);
            }

            private void applyStyle(boolean selected) {
                setStyle(selected
                        ? "-fx-background-color: rgba(230,126,34,0.4); -fx-border-color: #e67e22; -fx-border-radius: 4; -fx-text-fill: #ffd700; -fx-font-family: 'MedievalSharp', serif; -fx-font-size: 20px;"
                        : "-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #fdf5e6; -fx-font-family: 'MedievalSharp', serif; -fx-font-size: 20px; -fx-effect: dropshadow(three-pass-box, black, 8, 0.0, 0, 2);");
            }
        });
    }

    private void setupBindings() {
        createGameButton.disableProperty().bind(
                nicknameField.textProperty().isEmpty()
                        .or(maxPlayersComboBox.valueProperty().isNull()));
        joinGameButton.disableProperty().bind(
                nicknameField.textProperty().isEmpty()
                        .or(gamesListView.getSelectionModel().selectedItemProperty().isNull()));
    }

    private void setupVideoAndStage() {
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
    }

    public void render(MatchmakingViewState state) {
        if (!state.error().isEmpty()) errorLabel.setText(state.error());
        if (state.games() != null)   gamesListView.getItems().setAll(state.games());
    }

    @Override
    public void onExit() { videoBackground.stop(); }

    @Override
    public void refresh()
    { presenter.refresh(); }

    @Override
    public void handleWindowClose(WindowEvent event, GuiContext ctx, GuiNavigator navigator) {
        presenter.handleWindowClose(event);
    }

    @FXML private void onCreateGame() {
        errorLabel.setText("");
        presenter.createGame(nicknameField.getText().trim(), maxPlayersComboBox.getValue());
    }

    @FXML private void onJoinGame() {
        errorLabel.setText("");
        presenter.joinGame(nicknameField.getText().trim(),
                gamesListView.getSelectionModel().getSelectedItem());
    }

    @FXML private void onRefreshList() {
        errorLabel.setText("");
        presenter.refreshList();
    }

    @FXML private void handleDisconnect() { presenter.disconnect(); }
}
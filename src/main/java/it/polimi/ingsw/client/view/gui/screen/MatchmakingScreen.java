package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiAssetPaths;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.media.VideoBackground;
import it.polimi.ingsw.client.view.gui.presenter.MatchmakingPresenter;
import it.polimi.ingsw.client.view.gui.presenter.MatchmakingScreenPort;
import it.polimi.ingsw.client.view.gui.viewstate.MatchmakingViewState;
import it.polimi.ingsw.common.network.dto.GameInfoDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MatchmakingScreen  implements RefreshableScreen, MatchmakingScreenPort {

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
    }

    private void setupStyles() {
        createGameButton.getStyleClass().add("mm-button");
        joinGameButton.getStyleClass().add("mm-button");
        nicknameField.getStyleClass().add("mm-field");
        maxPlayersComboBox.getStyleClass().add("mm-field");
        maxPlayersComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText((!empty && item != null) ? item.toString() : null);
                if (!getStyleClass().contains("mm-combo-cell"))
                    getStyleClass().add("mm-combo-cell");
            }
        });
        errorLabel.getStyleClass().add("mm-error-label");
        gamesListView.getStyleClass().add("mm-list");
        gamesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(GameInfoDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("mm-list-cell", "mm-list-cell-selected");
                    return;
                }
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
                getStyleClass().removeAll("mm-list-cell", "mm-list-cell-selected");
                getStyleClass().add(selected ? "mm-list-cell-selected" : "mm-list-cell");
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
        errorLabel.setText(state.error());
        gamesListView.getItems().setAll(state.games());
    }

    @Override
    public void onEnter() {
        presenter.onScreenReady(this);
    }

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
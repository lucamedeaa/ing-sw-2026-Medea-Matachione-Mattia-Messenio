package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.FxActionRender;
import it.polimi.ingsw.client.view.gui.GuiAssetPaths;
import it.polimi.ingsw.client.view.gui.SceneId;
import it.polimi.ingsw.client.view.gui.viewstate.ActionsViewState;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

public class ActionsPanelController {

    private InGamePanelHost parentScreen;
    private ActionsViewState currentState;
    @FXML private Button skipButton;

    public void setParentScreen(InGamePanelHost parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void render(ActionsViewState state) {
        this.currentState = state;
        if (!state.isMyTurn()) return;
        FxActionRender render = new FxActionRender(this);
        for (ActionDto action : state.actions()) {
            action.accept(render);
        }
    }

    public void enableTakeCard(int upperPicks, int lowerPicks) {
        if (parentScreen == null || currentState == null) return;
        parentScreen.promptCardSelection(
                upperPicks, lowerPicks,
                currentState.affordableCardIds(),
                currentState.unaffordableSelectableCardIds()
        );
    }

    public void enablePlaceTotem(java.util.List<Integer> availableTiles) {
        if (parentScreen != null) parentScreen.promptTotemPlacement(availableTiles);
    }

    public void enableSkip() {
        skipButton.setDisable(false);
    }

    @FXML
    private void handleSkip() {
        if (parentScreen != null) parentScreen.skipAction();
    }

    @FXML
    private void handleReturnToMainMenu() {
        if (parentScreen != null) parentScreen.leaveGame();
    }

    @FXML
    private void handleToggleLog() {
        if (parentScreen != null) parentScreen.toggleLog();
    }

    private javafx.stage.Stage infoStage;

    @FXML
    private void handleShowInfo() {
        if (infoStage != null && infoStage.isShowing()) {
            infoStage.toFront();
            return;
        }
        try {
            javafx.fxml.FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneId.INFO.path()));
            javafx.scene.Parent root = loader.load();
            infoStage = new javafx.stage.Stage();
            infoStage.setTitle("Mesos - Reference Guide");
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(GuiAssetPaths.STYLE_CSS);
            infoStage.setScene(scene);
            if (skipButton != null && skipButton.getScene() != null)
                infoStage.initOwner(skipButton.getScene().getWindow());
            infoStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            infoStage.setResizable(false);
            infoStage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDisconnect() {
        if (parentScreen != null) parentScreen.disconnect();
    }
}
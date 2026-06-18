package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.FxActionRender;
import it.polimi.ingsw.client.view.gui.viewstate.ActionsViewState;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.List;

public class ActionsPanelController {

    private ActionCommandHost parentScreen;
    private ActionsViewState currentState;
    @FXML private Button skipButton;

    public void setParentScreen(ActionCommandHost parentScreen) {
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

    public void enablePlaceTotem(List<Integer> availableTiles) {
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

    @FXML
    private void handleDisconnect() {
        if (parentScreen != null) parentScreen.disconnect();
    }

    @FXML
    private void handleShowInfo() {
        if (parentScreen != null) parentScreen.showInfo();
    }
}
package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.FxActionRender;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.List;

public class ActionsPanelController {

    private GuiContext ctx;
    private InGameScreen parentScreen; // <-- Riferimento al mediatore

    @FXML private Button skipButton;



    public void setContext(GuiContext ctx) {
        this.ctx = ctx;
    }

    // <-- Nuovo setter per collegare il figlio al padre
    public void setParentScreen(InGameScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void refresh(GameModel model, String myNickname) {
        // Reset totale

        if (!myNickname.equals(model.getActivePlayer())) {
            return;
        }

        FxActionRender render = new FxActionRender(this);
        for (ActionDto action : model.getMyActions()) {
            action.accept(render);
        }
    }

// --- Metodi chiamati da FxActionRender ---

    public void enableTakeCard(int upperPicks, int lowerPicks) {
        if (parentScreen != null) parentScreen.promptCardSelection(upperPicks, lowerPicks);
    }

    public void enablePlaceTotem(java.util.List<Integer> availableTiles) {
        if (parentScreen != null) parentScreen.promptTotemPlacement(availableTiles);
    }

    public void enableSkip() {
        skipButton.setDisable(false);
    }

    // --- Handler degli eventi di JavaFX ---

    @FXML
    private void handleSkip() {
        ctx.controller().skipAction();
    }


    @FXML
    private void handleToggleLog() {
        if (parentScreen != null) {
            parentScreen.toggleLog();
        }
    }
    @FXML
    private void handleShowInfo() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/InfoScreen.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage infoStage = new javafx.stage.Stage();
            infoStage.setTitle("Reference Guide");
            infoStage.setScene(new javafx.scene.Scene(root));
            infoStage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDisconnect() {
        ctx.controller().disconnect(() -> {
            javafx.application.Platform.runLater(() -> {
                if (parentScreen != null) {
                    parentScreen.onServerDisconnected("Disconnesso volontariamente.");
                }
            });
        });
    }
}
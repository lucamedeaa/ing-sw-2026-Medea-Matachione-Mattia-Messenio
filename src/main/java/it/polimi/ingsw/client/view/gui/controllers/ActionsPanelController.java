package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.FxActionRender;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.List;

public class ActionsPanelController {

    private InGameScreen parentScreen;
    @FXML private Button skipButton;


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

//Metodi chiamati da FxActionRender
    public void enableTakeCard(int upperPicks, int lowerPicks) {
        if (parentScreen != null) parentScreen.promptCardSelection(upperPicks, lowerPicks);
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
        if (parentScreen != null) {
            parentScreen.toggleLog();
        }
    }

    private javafx.stage.Stage infoStage;

    @FXML
    private void handleShowInfo() {
        // Se la finestra è già aperta, portala in primo piano ed evita duplicazioni
        if (infoStage != null && infoStage.isShowing()) {
            infoStage.toFront();
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/InfoScreen.fxml"));
            javafx.scene.Parent root = loader.load();

            infoStage = new javafx.stage.Stage();
            infoStage.setTitle("Mesos - Reference Guide");

            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            // Inietta il foglio di stile globale per ereditare font medievali e configurazioni CSS
            if (getClass().getResource("/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            }

            infoStage.setScene(scene);

            // Associa la finestra pop-up alla schermata di gioco corrente
            if (skipButton != null && skipButton.getScene() != null) {
                infoStage.initOwner(skipButton.getScene().getWindow());
            }

            // Blocca l'interazione con il tabellone sottostante finché la guida è aperta
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
package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen; // <-- Importa la screen padre
import it.polimi.ingsw.client.view.gui.visitor.FxActionVisitor;
import it.polimi.ingsw.common.network.dto.action.ActionDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.List;

public class ActionsPanelController {

    private ServerController serverController;
    private InGameScreen parentScreen; // <-- Riferimento al mediatore

    @FXML private Button placeTotemButton;
    @FXML private Button takeCardButton;
    @FXML private Button skipButton;

    // Variabili per mantenere lo stato dei click validi
    private List<Integer> validTotemTiles;
    private int upperPicksAllowed;
    private int lowerPicksAllowed;

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    // <-- Nuovo setter per collegare il figlio al padre
    public void setParentScreen(InGameScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void refresh(GameModel model, String myNickname) {
        // 1. Reset totale
        placeTotemButton.setDisable(true);
        takeCardButton.setDisable(true);
        skipButton.setDisable(true);
        this.validTotemTiles = null;
        this.upperPicksAllowed = 0;
        this.lowerPicksAllowed = 0;

        if (!myNickname.equals(model.getActivePlayer())) {
            return;
        }

        // 2. Il Visitor "accende" i bottoni
        FxActionVisitor visitor = new FxActionVisitor(this);
        for (ActionDto action : model.getMyActions()) {
            action.accept(visitor);
        }
    }

    // --- Metodi chiamati dal FxActionVisitor ---

    public void enablePlaceTotem(List<Integer> availableTiles) {
        this.validTotemTiles = availableTiles;
        placeTotemButton.setDisable(false);
    }

    public void enableTakeCard(int upperPicks, int lowerPicks) {
        this.upperPicksAllowed = upperPicks;
        this.lowerPicksAllowed = lowerPicks;
        takeCardButton.setDisable(false);
    }

    public void enableSkip() {
        skipButton.setDisable(false);
    }

    // --- Handler degli eventi di JavaFX ---

    @FXML
    private void handleSkip() {
        // Skip non richiede click sul tabellone, si può inviare subito
        if (serverController != null) {
            serverController.skipAction();
        }
    }

    @FXML
    private void handleTakeCard() {
        // Delega la logica di transizione di stato al mediatore
        if (parentScreen != null) {
            parentScreen.startTakeCardFlow(upperPicksAllowed, lowerPicksAllowed);
        }
    }

    @FXML
    private void handlePlaceTotem() {
        // Delega la logica di transizione di stato al mediatore
        if (parentScreen != null) {
            parentScreen.startPlaceTotemFlow(validTotemTiles);
        }
    }
}
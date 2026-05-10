package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.util.List;

public class BoardPanelController {

    private InGameScreen parentScreen;

    @FXML private GridPane boardGrid;      // Griglia per il tabellone comune
    @FXML private HBox tribeContainer;    // Contenitore per le carte della tribù

    // Stati per la selezione
    private boolean canPickUpper = false;
    private boolean canPickLower = false;
    private List<Integer> validTotemTiles = null;

    public void setParentScreen(InGameScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    /**
     * Refresh principale del tabellone e della tribù visualizzata.
     */
    public void refresh(GameModel model, String targetNickname) {
        // 1. Rendering del tabellone comune (Upper e Lower Row)
        renderCommonBoard(model.getUpperRowCards(), model.getLowerRowCards());

        // 2. Rendering della tribù del giocatore selezionato
        renderTribe(model.getTribes().get(targetNickname));
    }

    private void renderCommonBoard(List<Integer> upper, List<Integer> lower) {
        // Logica per riempire boardGrid con ImageView delle carte
        // Ogni ImageView deve avere un setOnMouseClicked -> handleCardClick(row, col)
    }

    private void renderTribe(List<Integer> cardIds) {
        tribeContainer.getChildren().clear();
        if (cardIds == null) return;

        for (Integer id : cardIds) {
            // Esempio generico di caricamento immagine
            ImageView cardView = new ImageView(new Image("/images/cards/" + id + ".png"));
            cardView.setFitHeight(100);
            cardView.setPreserveRatio(true);
            tribeContainer.getChildren().add(cardView);
        }
    }

    // --- Metodi per la Macchina a Stati (chiamati da InGameScreen) ---

    public void enableCardSelection(boolean upperAllowed, boolean lowerAllowed) {
        this.canPickUpper = upperAllowed;
        this.canPickLower = lowerAllowed;
        // Feedback visivo: es. cambia opacità o aggiungi bordo alle righe cliccabili
    }

    public void enableTotemSelection(List<Integer> availableTiles) {
        this.validTotemTiles = availableTiles;
        // Feedback visivo sugli slot del tracciato totem
    }

    public void disableSelection() {
        this.canPickUpper = false;
        this.canPickLower = false;
        this.validTotemTiles = null;
    }

    // --- Handler dei click ---

    private void handleCardClick(int row, int col) {
        if (parentScreen == null) return;
        if (row == 0 && !canPickUpper) return;
        if (row == 1 && !canPickLower) return;

        parentScreen.onCardSelected(row, col);
    }
    private void handleTotemClick(int tileIndex) {
        if (parentScreen == null) return;
        if (validTotemTiles == null || !validTotemTiles.contains(tileIndex)) return;
        parentScreen.onTotemPositionSelected(tileIndex);
    }
}
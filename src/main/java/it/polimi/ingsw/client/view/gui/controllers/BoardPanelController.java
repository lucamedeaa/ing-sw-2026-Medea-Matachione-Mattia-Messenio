package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.controllers.board.*;
import it.polimi.ingsw.client.view.gui.interaction.BoardView;
import it.polimi.ingsw.client.view.gui.viewstate.BoardViewState;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BoardPanelController implements BoardView {
    @FXML private GridPane boardGrid;

    private final DoubleProperty cardWidthProp = new SimpleDoubleProperty(60.0);
    private final List<Pane> trackOverlays = new ArrayList<>();
    private int currentBoardPlayerCount = 0;
    private int currentColumn = 7;
    private BoardRenderer boardRenderer;
    private TotemRenderer totemRenderer;
    private BoardInteractionController boardInteraction;

    public void setListener(BoardSelectionListener listener) {
        boardInteraction.setListener(listener);
    }

    @FXML
    public void initialize() {
        CardNodeFactory factory = new CardNodeFactory(cardWidthProp);
        boardRenderer   = new BoardRenderer(boardGrid, factory, trackOverlays);
        totemRenderer   = new TotemRenderer(trackOverlays, factory);
        boardInteraction = new BoardInteractionController(boardGrid, trackOverlays);

        Platform.runLater(() -> {
            if (boardGrid.getParent() instanceof Region parent) {
                parent.widthProperty().addListener((o,old,n) -> updateOptimalSize(parent.getWidth(), parent.getHeight(), currentColumn));
                parent.heightProperty().addListener((o,old,n) -> updateOptimalSize(parent.getWidth(), parent.getHeight(), currentColumn));
                updateOptimalSize(parent.getWidth(), parent.getHeight(), currentColumn);
            }
        });
    }

    public void render(BoardViewState state) {
        // già sul thread JavaFX — niente Platform.runLater
        currentColumn = calculateCurrentColumns(state.playerCount(), state.upperCards().size());
        if (currentBoardPlayerCount != state.playerCount()) {
            boardGrid.getChildren().clear();
            boardRenderer.buildBoardTrack(state.playerCount());
            currentBoardPlayerCount = state.playerCount();
        }
        boardRenderer.renderCards(state.upperCards(), state.lowerCards());
        totemRenderer.renderTurnOrderTotems(state);
    }

    public void enableCardSelection(boolean upper, boolean lower,
                                    Set<Integer> affordableIds, Set<Integer> unaffordableIds) {
        boardInteraction.enableCardSelection(upper, lower, affordableIds, unaffordableIds);
    }

    public void highlightTotemPlacement(List<Integer> validIndices, boolean iAmOnOffer) {
        boardInteraction.highlightTotemPlacement(validIndices, iAmOnOffer);
    }

    public void disableAllInteractions() {
        boardInteraction.disableAllInteractions();
    }

    private int calculateCurrentColumns(int playerCount, int upperCardsCount) {
        int trackCols = playerCount > 0 ? BoardLayoutProvider.getTileLayout(playerCount).size() : 7;
        return Math.max(trackCols, upperCardsCount + 1);
    }

    private void updateOptimalSize(double w, double h, int cols) {
        double effectiveH = h - 100;
        double fromW = (w - 200) / cols;
        double fromH = (effectiveH / 3.0) / 1.62;
        cardWidthProp.set(Math.max(20.0, Math.min(fromW, fromH)));
    }
}
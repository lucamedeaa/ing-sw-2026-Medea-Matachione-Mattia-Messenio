package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.controllers.board.*;
import it.polimi.ingsw.client.view.gui.interaction.BoardView;
import it.polimi.ingsw.client.view.gui.viewstate.BoardViewState;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
            if (boardGrid.getScene() != null) {
                setupSceneListeners(boardGrid.getScene());
            } else {
                boardGrid.sceneProperty().addListener((obs, oldS, newS) -> {
                    if (newS != null) setupSceneListeners(newS);
                });
            }
        });
    }

    private void setupSceneListeners(Scene scene) {
        //Subtract 250px for the right-side panels and 220px for the bottom panel`
        scene.widthProperty().addListener((o, old, n) ->
            updateOptimalSize(n.doubleValue() - 250, scene.getHeight() - 220, currentColumn));

        scene.heightProperty().addListener((o, old, n) ->
            updateOptimalSize(scene.getWidth() - 250, n.doubleValue() - 220, currentColumn));

        updateOptimalSize(scene.getWidth() - 250, scene.getHeight() - 220, currentColumn);
    }

    public void render(BoardViewState state) {
        int newCols = calculateCurrentColumns(state.playerCount(), state.upperCards().size());
        if (currentBoardPlayerCount != state.playerCount() || currentColumn != newCols) {
            currentColumn = newCols;
            boardGrid.getChildren().clear();

            setupGridConstraints(currentColumn);
            boardRenderer.buildBoardTrack(state.playerCount());
            currentBoardPlayerCount = state.playerCount();
        }

        Scene scene = boardGrid.getScene();
        if (scene != null) {
        updateOptimalSize(scene.getWidth() - 250, scene.getHeight() - 220, currentColumn);
        }

        boardRenderer.renderCards(state.upperCards(), state.lowerCards());
        boardRenderer.renderDeck(state.nextDeckEra());
        boardRenderer.renderBuildingDecks(state.currentEra(), state.playerCount());
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
        int trackCols = playerCount > 0 ? BoardLayoutProvider.getTileLayout(playerCount).size() + 2 : 7;
        return Math.max(trackCols, upperCardsCount + 1);
    }

    private void updateOptimalSize(double w, double h, int cols) {
        double effectiveH = h - 100;
        double fromW = (w - 200) / cols;
        double fromH = (effectiveH / 3.0) / 1.62; // 3 card rows; 1.62 = card height/width ratio
        cardWidthProp.set(Math.max(20.0, Math.min(fromW, fromH)));
    }

    private void setupGridConstraints(int totalColumns) {
        boardGrid.getColumnConstraints().clear();
        for (int i = 0; i < totalColumns; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.minWidthProperty().bind(cardWidthProp);
            cc.prefWidthProperty().bind(cardWidthProp);
            cc.maxWidthProperty().bind(cardWidthProp);
            boardGrid.getColumnConstraints().add(cc);
        }
    }
}
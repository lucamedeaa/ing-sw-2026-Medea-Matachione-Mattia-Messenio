package it.polimi.ingsw.client.view.gui.controllers.board;

import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.List;

public class BoardInteractionController {

    private final GridPane boardGrid;
    private final List<Pane> trackOverlays;
    private BoardSelectionListener listener;

    public BoardInteractionController(GridPane boardGrid, List<Pane> trackOverlays) {
        this.boardGrid = boardGrid;
        this.trackOverlays = trackOverlays;
    }

    public void setListener(BoardSelectionListener listener) {
        this.listener = listener;
    }

    public void enableCardSelection(boolean upper, boolean lower, PlayerSnapshot player) {
        Platform.runLater(() -> {
            for (Node node : boardGrid.getChildren()) {
                Integer row = GridPane.getRowIndex(node);
                if (row == null) continue;

                node.getStyleClass().removeAll("card-glow-green", "card-glow-red");
                node.setCursor(Cursor.DEFAULT);
                node.setOnMouseClicked(null);

                if (row != 0 && row != 2) continue; // solo righe carte

                boolean isEvent = false;
                boolean canAfford = true;
                if (node.getUserData() instanceof Integer cardId) {
                    isEvent = CardAffordabilityPolicy.isEvent(cardId);
                    canAfford = CardAffordabilityPolicy.isAffordable(cardId, player);
                }

                boolean rowAllowed = (row == 0 && upper) || (row == 2 && lower);
                if (!rowAllowed || isEvent) continue;

                Integer colIdx = GridPane.getColumnIndex(node);
                if (colIdx == null) continue;
                int logicalRow = (row == 0) ? 0 : 1;
                int logicalCol = colIdx - 1;

                // Stile visivo: verde se affordable, rosso altrimenti
                if (canAfford) {
                    node.getStyleClass().add("card-glow-green");
                    node.setCursor(Cursor.HAND);
                } else {
                    node.getStyleClass().add("card-glow-red");
                }
                node.setOnMouseClicked(e -> handleCardClick(logicalRow, logicalCol));
            }
        });
    }

    /** iAmOnOffer calcolato da InGameScreen prima di chiamare questo metodo */
    public void highlightTotemPlacement(List<Integer> validIndices, boolean iAmOnOffer) {
        Platform.runLater(() -> {
            if (!iAmOnOffer) {
                if (trackOverlays.isEmpty()) return;
                Pane turnOrder = trackOverlays.get(0);
                turnOrder.setMouseTransparent(false);
                turnOrder.setCursor(Cursor.HAND);
                if (!turnOrder.getStyleClass().contains("tile-glow-white"))
                    turnOrder.getStyleClass().add("tile-glow-white");
                turnOrder.setOnMouseClicked(e -> {
                    turnOrder.getStyleClass().remove("tile-glow-white");
                    turnOrder.setMouseTransparent(true);
                    turnOrder.setOnMouseClicked(null);
                    showOfferTileOptions(validIndices);
                });
            }
            // se iAmOnOffer: nessun highlight (come nell'originale)
        });
    }

    public void disableAllInteractions() {
        Platform.runLater(() -> {
            clearTrackHighlights();
            for (Node node : boardGrid.getChildren()) {
                Integer row = GridPane.getRowIndex(node);
                if (row != null && (row == 0 || row == 2)) {
                    node.getStyleClass().removeAll("card-glow-green", "card-glow-red");
                    node.setEffect(null);
                    node.setCursor(Cursor.DEFAULT);
                    node.setOnMouseClicked(null);
                }
            }
        });
    }

    public void clearTrackHighlights() {
        for (Pane p : trackOverlays) {
            p.getStyleClass().removeAll("tile-glow-white", "tile-glow-yellow");
            p.setMouseTransparent(true);
            p.setCursor(Cursor.DEFAULT);
            p.setOnMouseClicked(null);
        }
    }

    private void showOfferTileOptions(List<Integer> validIndices) {
        for (Integer idx : validIndices) {
            int visualCol = idx + 1;

            if (visualCol >= trackOverlays.size()) continue;

            Pane p = trackOverlays.get(visualCol);
            p.setMouseTransparent(false);
            p.setCursor(Cursor.HAND);

            if (!p.getStyleClass().contains("tile-glow-yellow"))
                p.getStyleClass().add("tile-glow-yellow");
            p.setOnMouseClicked(e -> {
                clearTrackHighlights();
                if (listener != null) listener.onTotemPositionSelected(idx);
            });
        }
    }

    private void handleCardClick(int logicalRow, int logicalCol) {
        if (listener != null) listener.onCardSelected(logicalRow, logicalCol);
    }
}
package it.polimi.ingsw.client.view.gui.controllers.board;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import java.util.List;
import java.util.Set;

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

    public void enableCardSelection(boolean upper, boolean lower, Set<Integer> affordableIds, Set<Integer> unaffordableIds) {
            for (Node node : boardGrid.getChildren()) {
                Integer row = GridPane.getRowIndex(node);
                if (row == null) continue;

                node.getStyleClass().removeAll("card-glow-green", "card-glow-red");
                node.setCursor(Cursor.DEFAULT);
                node.setOnMouseClicked(null);

                if (row != 0 && row != 2) continue;

                boolean rowAllowed = (row == 0 && upper) || (row == 2 && lower);
                if (!rowAllowed) continue;

                if (!(node.getUserData() instanceof Integer cardId)) continue;

                boolean isAffordable   = affordableIds.contains(cardId);
                boolean isUnaffordable = unaffordableIds.contains(cardId);
                if (!isAffordable && !isUnaffordable) continue; // neither affordable nor unaffordable -> event card, not selectable

                Integer colIdx = GridPane.getColumnIndex(node);
                if (colIdx == null) continue;
                int logicalRow = (row == 0) ? 0 : 1; //grid row 0/2 -> logical upper(0)/lower(1)
                int logicalCol = colIdx - 1; //column 0 holds the deck, card start at 1

                if (isAffordable) {
                    node.getStyleClass().add("card-glow-green");
                    node.setCursor(Cursor.HAND);
                } else {
                    node.getStyleClass().add("card-glow-red");
                }
                node.setOnMouseClicked(e -> handleCardClick(logicalRow, logicalCol));
            }
    }

    /** iAmOnOffer is computed by InGameScreen before this method is called. */
    public void highlightTotemPlacement(List<Integer> validIndices, boolean iAmOnOffer) {
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
    }

    public void disableAllInteractions() {
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
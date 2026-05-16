package it.polimi.ingsw.client.view.gui.controllers.board;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.List;

public class BoardRenderer {
    private final GridPane boardGrid;
    private final CardNodeFactory factory;
    private final List<Pane> trackOverlays;

    public BoardRenderer(GridPane boardGrid, CardNodeFactory factory, List<Pane> trackOverlays) {
        this.boardGrid = boardGrid;
        this.factory = factory;
        this.trackOverlays = trackOverlays;
    }

    public void buildBoardTrack(int playerCount) {
        boardGrid.getColumnConstraints().clear();
        boardGrid.getRowConstraints().clear();
        trackOverlays.clear();
        List<String> layout = BoardLayoutProvider.getTileLayout(playerCount);
        for (int col = 0; col < layout.size(); col++) {
            CardNodeFactory.TileNode tile = factory.createTileNode(layout.get(col));
            if (tile == null) continue;
            if (col == 0) GridPane.setMargin(tile.container(), new Insets(0, 15, 0, 0));
            boardGrid.add(tile.container(), col, 1);
            trackOverlays.add(tile.overlay());
        }
    }

    public void renderCards(List<Integer> upper, List<Integer> lower) {
        boardGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && (row == 0 || row == 2);
        });
        for (int i = 0; i < upper.size(); i++) {
            Integer id = upper.get(i);
            if (id != null) { StackPane p = factory.createCardContainer(id); if (p!=null) boardGrid.add(p, i+1, 0); }
        }
        for (int i = 0; i < lower.size(); i++) {
            Integer id = lower.get(i);
            if (id != null) { StackPane p = factory.createCardContainer(id); if (p!=null) boardGrid.add(p, i+1, 2); }
        }
    }
}

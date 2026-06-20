package it.polimi.ingsw.client.view.gui.controllers.board;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * Renders board cards, tracks, and deck backs into the board grid.
 */
public class BoardRenderer {
    private final GridPane boardGrid;
    private final CardNodeFactory factory;
    private final List<Pane> trackOverlays;

    /**
     * Creates a board renderer.
     *
     * @param boardGrid board grid to populate
     * @param factory node factory used to create card and tile nodes
     * @param trackOverlays overlays collected for track interactions
     */
    public BoardRenderer(GridPane boardGrid, CardNodeFactory factory, List<Pane> trackOverlays) {
        this.boardGrid = boardGrid;
        this.factory = factory;
        this.trackOverlays = trackOverlays;
    }

    /**
     * Builds the static turn-order and offer-track tile row.
     *
     * @param playerCount number of players
     */
    public void buildBoardTrack(int playerCount) {
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

    // Grid layout: row 0 = upper cards, row 1 = track/tiles, row 2 = lower cards.
    // Column 0 is reserved for the main deck, so playable cards start at column 1.
    /**
     * Renders upper and lower board card rows.
     *
     * @param upper upper-row card identifiers
     * @param lower lower-row card identifiers
     */
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

    /**
     * Renders the main deck back.
     *
     * @param nextDeckEra era of the next deck card
     */
    public void renderDeck(Integer nextDeckEra) {

        boardGrid.getChildren().removeIf(node -> {
            Integer col = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            return col != null && col == 0 && row != null && row == 0;
        });

        if (nextDeckEra == null) return;

        StackPane deckPane = factory.createDeckBackContainer(nextDeckEra);
        if (deckPane != null) {
            boardGrid.add(deckPane, 0, 0);
        }
    }

    /**
     * Renders future building deck backs.
     *
     * @param currentEra current game era
     * @param playerCount number of players
     */
    public void renderBuildingDecks(Integer currentEra, int playerCount) {
        int baseTrackSize = trackOverlays.size();


        boardGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);
            return row != null && row == 1 && col != null && col >= baseTrackSize;
        });

        if (currentEra == null) return;

        int MAX_ERA = 3;

        for (int era = currentEra + 1; era <= MAX_ERA; era++) {
            addBuildingDecks(era, playerCount);
        }
    }

    private void addBuildingDecks(int era, int playerCount) {
        StackPane pane = factory.createBuildingDeckBackContainer(era);
        int col = BoardLayoutProvider.getBuildingDeckColumn(playerCount, era);
        if (pane != null) {
            boardGrid.add(pane, col, 1);
        }
    }

}

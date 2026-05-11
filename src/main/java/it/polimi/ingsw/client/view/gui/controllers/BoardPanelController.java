package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiAssetManager;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;


import java.util.List;

public class BoardPanelController {

    private InGameScreen parentScreen;

    @FXML private GridPane boardGrid;      // Griglia per il tabellone comune
    @FXML private HBox tribeContainer;    // Contenitore per le carte della tribù

    private int currentBoardPlayerCount = 0;

    // Stati per la selezione
    private boolean canPickUpper = false;
    private boolean canPickLower = false;
    private List<Integer> validTotemTiles = null;

    public void setParentScreen(InGameScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void refresh(GameModel model, String targetNickname) {
        int playerCount = model.getPlayers().size();

        Platform.runLater(() -> {
            if (currentBoardPlayerCount != playerCount) {
                boardGrid.getChildren().clear(); // Pulisce residui di partite precedenti
                buildBoardTrack(playerCount);
                currentBoardPlayerCount = playerCount;
            }

            renderCards(model.getUpperRowCards(), model.getLowerRowCards());
            renderTribe(model.getTribes().get(targetNickname));
        });
    }

    /**
     * Costruisce la riga centrale delle tessere in base al numero di giocatori.
     */
    private void buildBoardTrack(int playerCount) {
        // Ricava la sequenza di tessere esatta.
        // L'elemento "TO" rappresenta la Turn Order Tile.
        List<String> layout = getTileLayout(playerCount);

        for (int col = 0; col < layout.size(); col++) {
            String tileCode = layout.get(col);

            Image img = GuiAssetManager.getTileImage(tileCode);
            if (img == null) continue; // Evita NullPointerException se manca il file

            ImageView tileView = new ImageView(img);
            tileView.setFitWidth(120); // Regola in base alle tue immagini
            tileView.setPreserveRatio(true);

            if (col == 0) {
            // Insets(top, right, bottom, left) -> 15 pixel di spazio a destra
            GridPane.setMargin(tileView, new Insets(0, 15, 0, 0));
            }
            // Inserisce l'immagine nella colonna corrente, alla riga 1 (quella centrale)
            boardGrid.add(tileView, col, 1);
        }
    }

    /**
     * Hardcoding della struttura del tabellone.
     * Sostituisci le lettere con quelle esatte usate nella tua TUI.
     */
    private List<String> getTileLayout(int playerCount) {
        return switch (playerCount) {
            default -> List.of("TURNORDER_TILE_2", "TILE_B", "TILE_C", "TILE_E", "TILE_F");
            case 3 -> List.of("TURNORDER_TILE_3", "TILE_B", "TILE_C", "TILE_D", "TILE_E", "TILE_F");
            case 4 -> List.of("TURNORDER_TILE_4", "TILE_B", "TILE_C", "TILE_D", "TILE_E", "TILE_F", "TILE_G");
            case 5 -> List.of("TURNORDER_TILE_5", "TILE_A", "TILE_B", "TILE_C", "TILE_D", "TILE_E", "TILE_F", "TILE_G");
        };
    }

//mi sa inutile, però vediamo
/*    private int[] turnOrderBonuses(int playerCount) {
        return switch(playerCount) {
            case 2 -> new int[]{1, -1};
            case 3 -> new int[]{1, 0, -1};
            case 4 -> new int[]{2, 1, 0, -1};
            default -> new int[]{3, 1, 0, 0, -1};
        };
    }*/


    private void renderCards(List<Integer> upper, List<Integer> lower) {
        // Rimuove solo le carte (riga 0 e riga 2), lasciando intatte le tessere (riga 1)
        boardGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && (row == 0 || row == 2);
        });

        // Disegna la riga superiore (Server Row 0 -> GUI Row 0)
        for (int i = 0; i < upper.size(); i++) {
            Integer cardId = upper.get(i);
            // Il server imposta 'null' se la carta è stata presa
            if (cardId != null) {
                addCardToGrid(cardId, 0, i + 1, 0, i);
            }
        }

        // Disegna la riga inferiore (Server Row 1 -> GUI Row 2)
        for (int i = 0; i < lower.size(); i++) {
            Integer cardId = lower.get(i);
            if (cardId != null) {
                addCardToGrid(cardId, 2, i + 1, 1, i);
            }
        }
    }

    private void addCardToGrid(int cardId, int visualRow, int visualCol, int logicalRow, int logicalCol) {
        Image img = GuiAssetManager.getCardImage(cardId);
        if (img == null) return;

        ImageView cardView = new ImageView(img);
        cardView.setFitWidth(90);
        cardView.setFitHeight(120);
        cardView.setPreserveRatio(false);
        cardView.setSmooth(true);

        // Crea una gabbia rigida che la griglia non può alterare
        javafx.scene.layout.StackPane rigidBox = new javafx.scene.layout.StackPane(cardView);
        rigidBox.setMinSize(90, 120);
        rigidBox.setMaxSize(90, 120);
        rigidBox.setPrefSize(90, 120);

        // Sposta il listener del click sulla gabbia
        rigidBox.setOnMouseClicked(e -> handleCardClick(logicalRow, logicalCol));

        // Aggiungi la gabbia alla griglia, non la singola immagine
        boardGrid.add(rigidBox, visualCol, visualRow);
    }

    private void renderTribe(List<Integer> tribeCards) {
        tribeContainer.getChildren().clear();

        for (Integer cardId : tribeCards) {
            Image img = GuiAssetManager.getCardImage(cardId);
            if (img == null) continue;

            ImageView cardView = new ImageView(img);

            // Imposta le dimensioni base desiderate per le carte della tribù
            cardView.setFitWidth(75);
            cardView.setFitHeight(100);
            cardView.setPreserveRatio(false);
            cardView.setSmooth(true);

            // Crea la gabbia rigida per l'HBox
            javafx.scene.layout.StackPane rigidBox = new javafx.scene.layout.StackPane(cardView);
            rigidBox.setMinSize(75, 100);
            rigidBox.setMaxSize(75, 100);
            rigidBox.setPrefSize(75, 100);

            // Aggiungi un margine opzionale direttamente alla gabbia se vuoi distanziare le carte
            // javafx.scene.layout.HBox.setMargin(rigidBox, new javafx.geometry.Insets(0, 5, 0, 0));

            tribeContainer.getChildren().add(rigidBox);
        }
    }

    // --- Metodi per la Macchina a Stati (chiamati da InGameScreen) ---

    public void enableCardSelection(boolean upperAllowed, boolean lowerAllowed) {
        this.canPickUpper = upperAllowed;
        this.canPickLower = lowerAllowed;

        Platform.runLater(() -> {
            for (Node node : boardGrid.getChildren()) {
                Integer row = GridPane.getRowIndex(node);
                if (row == null) continue;

                // Se è una carta della riga superiore e possiamo pescarla,
                // OPPURE se è della riga inferiore e possiamo pescarla:
                if ((row == 0 && upperAllowed) || (row == 2 && lowerAllowed)) {
                    node.setEffect(new DropShadow(20, Color.YELLOW));
                    node.setCursor(Cursor.HAND);
                } else if (row == 0 || row == 2) {
                    node.setEffect(null); // Spegne le carte non valide
                    node.setCursor(Cursor.DEFAULT);
                }
            }
        });
    }

    public void disableSelection() {
        this.canPickUpper = false;
        this.canPickLower = false;
        this.validTotemTiles = null;

        // Spegne tutto
        Platform.runLater(() -> {
             for (Node node : boardGrid.getChildren()) {
                 node.setEffect(null);
                 node.setCursor(Cursor.DEFAULT);
             }
        });
    }

    public void enableTotemSelection(List<Integer> availableTiles) {
        this.validTotemTiles = availableTiles;
        // Feedback visivo sugli slot del tracciato totem
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
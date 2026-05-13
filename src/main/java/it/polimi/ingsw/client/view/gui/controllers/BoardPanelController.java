package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.view.gui.GuiAssetManager;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import it.polimi.ingsw.server.model.enums.TotemColor;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;


import java.util.ArrayList;
import java.util.List;

public class BoardPanelController {

    private InGameScreen parentScreen;

    @FXML private GridPane boardGrid;      // Griglia per il tabellone comune

    private int currentBoardPlayerCount = 0;

    // Stati per la selezione
    private boolean canPickUpper = false;
    private boolean canPickLower = false;
    private List<Integer> validTotemTiles = null;
    private final DoubleProperty cardWidthProp = new SimpleDoubleProperty(60.0);
    private Pane turnOrderTotemOverlay;


    public void setParentScreen(InGameScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            if (boardGrid.getParent() instanceof Region parent) {
                parent.widthProperty().addListener((obs, old, newV) -> updateOptimalSize(parent.getWidth(), parent.getHeight()));
                parent.heightProperty().addListener((obs, old, newV) -> updateOptimalSize(parent.getWidth(), parent.getHeight()));
                // Prima chiamata per l'assetto iniziale
                updateOptimalSize(parent.getWidth(), parent.getHeight());
            }
        });
    }

    private void updateOptimalSize(double availableWidth, double availableHeight) {
        int cols = currentBoardPlayerCount > 0 ? getTileLayout(currentBoardPlayerCount).size() : 7;
        if (cols == 0) return;

        // availableHeight qui è l'altezza di tutto il pannello laterale (VBox).
        // Dobbiamo sottrarre lo spazio occupato dalla tribù, dai margini e dalle label (circa 250px)
        double effectiveHeightForBoard = availableHeight - 250;

        double maxWidthFromWidth = (availableWidth - 35) / cols;
        double maxCardHeight = (effectiveHeightForBoard - 12) / 3.0; // 3 righe, 12px totali di vgap
        double maxWidthFromHeight = maxCardHeight * 0.75;

        // Il blocco non supera mai l'altezza massima disponibile, ma si stringe se la larghezza non basta
        double optimal = Math.min(maxWidthFromWidth, maxWidthFromHeight);

        // Imposta un minimo vitale (es. 20px) per evitare errori di rendering a finestre compresse
        cardWidthProp.set(Math.max(20.0, optimal));
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
            renderTurnOrderTotems(model);
        });
    }

    /**
     * Costruisce la riga centrale delle tessere in base al numero di giocatori.
     */
    private void buildBoardTrack(int playerCount) {
        List<String> layout = getTileLayout(playerCount);
        int cols = layout.size();

        // Rimuovi esplicitamente eventuali vincoli preesistenti per permettere
        // al GridPane di "avvolgere" strettamente i contenuti
        boardGrid.getColumnConstraints().clear();
        boardGrid.getRowConstraints().clear();

        for (int col = 0; col < cols; col++) {

            String tileCode = layout.get(col);
            Image img = GuiAssetManager.getTileImage(tileCode);
            if (img == null) continue;

            ImageView tileView = new ImageView(img);
            tileView.setPreserveRatio(false);
            tileView.setSmooth(true);

            // Binding dell'immagine alla proprietà globale
            tileView.fitWidthProperty().bind(cardWidthProp);
            tileView.fitHeightProperty().bind(cardWidthProp.multiply(4.0/3.0));

            StackPane container = new StackPane(tileView);

            // Blocca le dimensioni del contenitore per impedire sbavature della griglia
            container.minWidthProperty().bind(cardWidthProp);
            container.maxWidthProperty().bind(cardWidthProp);
            container.prefWidthProperty().bind(cardWidthProp);

            container.minHeightProperty().bind(cardWidthProp.multiply(4.0/3.0));
            container.maxHeightProperty().bind(cardWidthProp.multiply(4.0/3.0));
            container.prefHeightProperty().bind(cardWidthProp.multiply(4.0/3.0));

            // Mantiene lo spazio per la turn order tile (colonna 0)
            if (col == 0) {
                GridPane.setMargin(container, new Insets(0, 15, 0, 0));

                turnOrderTotemOverlay = new Pane();
                //turnOrderTotemOverlay.setMouseTransparent(true);
                // Il Pane deve copiare esattamente le dimensioni del contenitore
                turnOrderTotemOverlay.prefWidthProperty().bind(container.widthProperty());
                turnOrderTotemOverlay.prefHeightProperty().bind(container.heightProperty());

                // Aggiungi l'overlay SOPRA la tileView
                container.getChildren().add(turnOrderTotemOverlay);
            }

            boardGrid.add(container, col, 1);
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
        cardView.setPreserveRatio(false);
        cardView.setSmooth(true);

        // Identico binding per le carte normali
        cardView.fitWidthProperty().bind(cardWidthProp);
        cardView.fitHeightProperty().bind(cardWidthProp.multiply(4.0/3.0));

        StackPane container = new StackPane(cardView);

        // Stessi vincoli di blocco del contenitore
        container.minWidthProperty().bind(cardWidthProp);
        container.maxWidthProperty().bind(cardWidthProp);
        container.prefWidthProperty().bind(cardWidthProp);

        container.minHeightProperty().bind(cardWidthProp.multiply(4.0/3.0));
        container.maxHeightProperty().bind(cardWidthProp.multiply(4.0/3.0));
        container.prefHeightProperty().bind(cardWidthProp.multiply(4.0/3.0));

        container.setOnMouseClicked(e -> handleCardClick(logicalRow, logicalCol));
        boardGrid.add(container, visualCol, visualRow);
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
    
    private void renderTurnOrderTotems(GameModel model) {
        if (turnOrderTotemOverlay == null) return;
        turnOrderTotemOverlay.getChildren().clear();

        // Iteriamo sui player come da tua indicazione
        List<PlayerSnapshot> players = new ArrayList<>(model.getPlayers().values());

        // Coordinate percentuali (0.0 - 1.0) dei quadratini bianchi sulla tessera
        // Nota: questi valori vanno calibrati millimetricamente sul tuo asset specifico
        double[] ySteps = {0.16, 0.33, 0.50, 0.67, 0.84};
        double xPercent = 0.15; // Posizione orizzontale della colonna di bianchi

        for (int i = 0; i < players.size() && i < ySteps.length; i++) {
            TotemColor color = players.get(i).getTotemColor();
            Image img = GuiAssetManager.getTotemImage(color.name());

            if (img != null) {
                ImageView totemView = new ImageView(img);
                totemView.setPreserveRatio(true);

                // Dimensione del totem: circa il 20% della larghezza della tessera
                totemView.fitWidthProperty().bind(cardWidthProp.multiply(0.20));

                // Ancoraggio dinamico: se la finestra si allarga, il totem si sposta col quadratino
                totemView.layoutXProperty().bind(turnOrderTotemOverlay.widthProperty().multiply(xPercent));
                totemView.layoutYProperty().bind(turnOrderTotemOverlay.heightProperty().multiply(ySteps[i]));

                turnOrderTotemOverlay.getChildren().add(totemView);
            }
        }
    }
}
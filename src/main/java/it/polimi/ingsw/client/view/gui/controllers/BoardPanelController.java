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
import java.util.Map;

public class BoardPanelController {

    private InGameScreen parentScreen;

    @FXML private GridPane boardGrid;      // Griglia per il tabellone comune

    private int currentBoardPlayerCount = 0;

    // Stati per la selezione
    private boolean canPickUpper = false;
    private boolean canPickLower = false;
    private List<Integer> validTotemTiles = null;
    private final DoubleProperty cardWidthProp = new SimpleDoubleProperty(60.0);
    private final List<Pane> trackOverlays = new ArrayList<>();


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
        trackOverlays.clear(); // Corretto: resetta la lista a ogni refresh

        for (int col = 0; col < cols; col++) {

            String tileCode = layout.get(col);
            Image img = GuiAssetManager.getTileImage(tileCode);
            if (img == null) continue;

            ImageView tileView = new ImageView(img);
            tileView.setPreserveRatio(false);
            tileView.setSmooth(true);

            // Binding dell'immagine alla proprietà globale (INTATTO)
            tileView.fitWidthProperty().bind(cardWidthProp);
            tileView.fitHeightProperty().bind(cardWidthProp.multiply(1.62));

            StackPane container = new StackPane(tileView);

            // Blocca le dimensioni del contenitore per impedire sbavature della griglia (INTATTO)
            container.minWidthProperty().bind(cardWidthProp);
            container.maxWidthProperty().bind(cardWidthProp);
            container.prefWidthProperty().bind(cardWidthProp);

            container.minHeightProperty().bind(cardWidthProp.multiply(1.62));
            container.maxHeightProperty().bind(cardWidthProp.multiply(1.62));
            container.prefHeightProperty().bind(cardWidthProp.multiply(1.62));

            // --- CREAZIONE OVERLAY PER TUTTE LE TESSERE ---
            Pane overlay = new Pane();
            overlay.setMouseTransparent(true);

            // Il Pane copia esattamente le dimensioni del contenitore, senza forzarne il resize
            overlay.minWidthProperty().bind(container.widthProperty());
            overlay.maxWidthProperty().bind(container.widthProperty());
            overlay.minHeightProperty().bind(container.heightProperty());
            overlay.maxHeightProperty().bind(container.heightProperty());

            // Aggiunge l'overlay sopra la tileView e lo salva nella lista
            container.getChildren().add(overlay);
            trackOverlays.add(overlay);

            // --- MARGINE SOLO PER LA TURN ORDER TILE ---
            if (col == 0) {
                GridPane.setMargin(container, new Insets(0, 15, 0, 0));
            }

            boardGrid.add(container, col, 1);
        }
    }
    /*private void buildBoardTrack(int playerCount) {      funziona il ridimensionamento bene
        List<String> layout = getTileLayout(playerCount);
        int cols = layout.size();

        // Rimuovi esplicitamente eventuali vincoli preesistenti per permettere
        // al GridPane di "avvolgere" strettamente i contenuti
        boardGrid.getColumnConstraints().clear();
        boardGrid.getRowConstraints().clear();
        trackOverlays.clear();

        for (int col = 0; col < cols; col++) {

            String tileCode = layout.get(col);
            Image img = GuiAssetManager.getTileImage(tileCode);
            if (img == null) continue;

            ImageView tileView = new ImageView(img);
            tileView.setPreserveRatio(false);
            tileView.setSmooth(true);

            // Binding dell'immagine alla proprietà globale
            tileView.fitWidthProperty().bind(cardWidthProp);
            tileView.fitHeightProperty().bind(cardWidthProp.multiply(1.62));

            StackPane container = new StackPane(tileView);

            // Blocca le dimensioni del contenitore per impedire sbavature della griglia
            container.minWidthProperty().bind(cardWidthProp);
            container.maxWidthProperty().bind(cardWidthProp);
            container.prefWidthProperty().bind(cardWidthProp);

            container.minHeightProperty().bind(cardWidthProp.multiply(1.62));
            container.maxHeightProperty().bind(cardWidthProp.multiply(1.62));
            container.prefHeightProperty().bind(cardWidthProp.multiply(1.62));

            // Mantiene lo spazio per la turn order tile (colonna 0)
            if (col == 0) {
                GridPane.setMargin(container, new Insets(0, 15, 0, 0));

                turnOrderTotemOverlay = new Pane();
                turnOrderTotemOverlay.setMouseTransparent(true);
                // Il Pane deve copiare esattamente le dimensioni del contenitore
                turnOrderTotemOverlay.minWidthProperty().bind(container.widthProperty());
                turnOrderTotemOverlay.maxWidthProperty().bind(container.widthProperty());
                turnOrderTotemOverlay.minHeightProperty().bind(container.heightProperty());
                turnOrderTotemOverlay.maxHeightProperty().bind(container.heightProperty());

                // Aggiungi l'overlay SOPRA la tileView
                container.getChildren().add(turnOrderTotemOverlay);
            }

            boardGrid.add(container, col, 1);
        }
    }*/

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
        cardView.fitHeightProperty().bind(cardWidthProp.multiply(1.62));

        StackPane container = new StackPane(cardView);

        // Stessi vincoli di blocco del contenitore
        container.minWidthProperty().bind(cardWidthProp);
        container.maxWidthProperty().bind(cardWidthProp);
        container.prefWidthProperty().bind(cardWidthProp);

        container.minHeightProperty().bind(cardWidthProp.multiply(1.62));
        container.maxHeightProperty().bind(cardWidthProp.multiply(1.62));
        container.prefHeightProperty().bind(cardWidthProp.multiply(1.62));

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
    
    private void renderTurnOrderTotems(GameModel model) {
        trackOverlays.forEach(p -> p.getChildren().clear());

        List<PlayerSnapshot> players = new ArrayList<>(model.getPlayers().values());
        double[] ySteps = getTurnOrderTotemYSteps(players.size());

        // Recupera la mappa delle posizioni
        Map<String, Integer> totemPositions = model.getTotemPositions();

        for (int i = 0; i < players.size(); i++) {
            PlayerSnapshot p = players.get(i);
            TotemColor color = p.getTotemColor();
            Image img = GuiAssetManager.getTotemImage(color);

            if (img != null) {
                ImageView totemView = new ImageView(img);
                totemView.setPreserveRatio(true);
                totemView.fitWidthProperty().bind(cardWidthProp.multiply(0.38));

                totemView.layoutXProperty().bind(cardWidthProp.subtract(totemView.fitWidthProperty()).divide(2));
                totemView.layoutYProperty().bind(cardWidthProp.multiply(1.62).multiply(ySteps[i]));

                String nickname = p.getNickname();
                Integer pos = totemPositions.get(nickname);

                // Se la posizione è null (non piazzato o tornato alla base) o -1, va sulla Turn Order Tile (col 0)
                int visualCol = (pos == null || pos == 0) ? 0 : pos;

                if (visualCol < trackOverlays.size()) {
                    Pane overlay = trackOverlays.get(visualCol);

                    if (visualCol == 0) {
                        // Posizione nel tracciato verticale (colonna 0)
                        totemView.layoutYProperty().bind(overlay.heightProperty().multiply(ySteps[i]));
                    } else {
                        // Posizione fissa in alto per le tessere offerta
                        totemView.layoutYProperty().bind(overlay.heightProperty().multiply(0.18));
                    }
                    overlay.getChildren().add(totemView);
                }
            }
        }
    }

    private double[] getTurnOrderTotemYSteps(int playerCount) {
        return switch (playerCount) {
            case 2 -> new double[]{0.23, 0.40};
            case 3 -> new double[]{0.16, 0.50, 0.84};
            case 4 -> new double[]{0.12, 0.37, 0.62, 0.87};
            default -> new double[]{0.10, 0.30, 0.50, 0.70, 0.90};
        };
    }

    public void highlightTotemPlacement(List<Integer> validIndices, String myNickname, GameModel model) {

        Platform.runLater(() -> {
            if (trackOverlays.isEmpty()) return;

            Pane turnOrder = trackOverlays.get(0);
            turnOrder.setMouseTransparent(false);
            turnOrder.setCursor(Cursor.HAND);

            // Aumentata opacità per renderlo visibile (0.6)
            turnOrder.setStyle("-fx-background-color: rgba(255, 255, 255, 0.6); -fx-border-color: white; -fx-border-width: 2;");

            turnOrder.setOnMouseClicked(e -> {
                turnOrder.setStyle("");
                turnOrder.setMouseTransparent(true);
                showOfferTileOptions(validIndices);
            });
        });
    }

    private void showOfferTileOptions(List<Integer> validIndices) {
        for (Integer idx : validIndices) {

            // Verifica come il server mappa le tessere offerta.
            // Se 1 è la prima Offer Tile, usa 'idx'. Se 0 è la prima Offer Tile, usa 'idx + 1'.
            int visualCol = idx; // o idx + 1

            if (visualCol >= trackOverlays.size()) continue;

            Pane p = trackOverlays.get(visualCol);
            p.setMouseTransparent(false);
            p.setCursor(Cursor.HAND);

            // Sostituisce l'effetto ombra rotto con un bordo luminoso e uno sfondo leggero
            p.setEffect(null);
            p.setStyle("-fx-border-color: white; -fx-border-width: 3; -fx-background-color: rgba(255, 255, 255, 0.6);");

            p.setOnMouseClicked(e -> {
                clearTrackHighlights();
                parentScreen.onTotemPositionSelected(idx); // Invia sempre l'indice originale al server
            });
        }
    }

    public void clearTrackHighlights() {
        for (Pane p : trackOverlays) {
            p.setStyle("");
            p.setEffect(null);
            p.setMouseTransparent(true);
            p.setCursor(Cursor.DEFAULT);
        }
    }
}
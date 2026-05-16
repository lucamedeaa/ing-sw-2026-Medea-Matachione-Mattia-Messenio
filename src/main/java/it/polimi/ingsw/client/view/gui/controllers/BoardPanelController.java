package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.view.gui.GuiAssetManager;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import it.polimi.ingsw.common.config.CardRegistry;
import it.polimi.ingsw.server.model.enums.TotemColor;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BoardPanelController {

    private InGameScreen parentScreen;

    @FXML
    private GridPane boardGrid;      // Griglia per il tabellone comune

    private int currentBoardPlayerCount = 0;

    // Stati per la selezione
    private boolean canPickUpper = false;
    private boolean canPickLower = false;
    private List<Integer> validTotemTiles = null;
    private final DoubleProperty cardWidthProp = new SimpleDoubleProperty(60.0);
    private final List<Pane> trackOverlays = new ArrayList<>();
    private int currentColumn = 7;


    public void setParentScreen(InGameScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (boardGrid.getParent() instanceof Region parent) {
                parent.widthProperty().addListener((obs, old, newV) -> updateOptimalSize(parent.getWidth(), parent.getHeight(), currentColumn));
                parent.heightProperty().addListener((obs, old, newV) -> updateOptimalSize(parent.getWidth(), parent.getHeight(), currentColumn));
                // Prima chiamata per l'assetto iniziale
                updateOptimalSize(parent.getWidth(), parent.getHeight(),  currentColumn);
            }
        });
    }

    private int calculateCurrentColumns(int playerCount, int upperCardsCount) {
        int trackCols = playerCount > 0 ? getTileLayout(playerCount).size() : 7;
        // +1 perché la colonna 0 è riservata alla Turn Order Tile
        int upperCols = upperCardsCount + 1;
        return Math.max(trackCols, upperCols);
    }

    private void updateOptimalSize(double availableWidth, double availableHeight, int cols) {

        // availableHeight qui è l'altezza di tutto il pannello laterale (VBox).
        // Dobbiamo sottrarre lo spazio occupato dalla tribù, dai margini e dalle label (circa 250px)
        double effectiveHeightForBoard = availableHeight - 100;

        double maxWidthFromWidth = (availableWidth - 200) / cols;
        double maxCardHeight = effectiveHeightForBoard / 3.0; // 3 righe, 12px totali di vgap
        double maxWidthFromHeight = maxCardHeight / 1.62;

        // Il blocco non supera mai l'altezza massima disponibile, ma si stringe se la larghezza non basta
        double optimal = Math.min(maxWidthFromWidth, maxWidthFromHeight);

        // Imposta un minimo vitale (es. 20px) per evitare errori di rendering a finestre compresse
        cardWidthProp.set(Math.max(20.0, optimal));
    }


    public void refresh(GameModel model, String targetNickname) {
        int playerCount = model.getPlayers().size();

        Platform.runLater(() -> {
            currentColumn = calculateCurrentColumns(playerCount, model.getUpperRowCards().size());
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


    private void renderCards(List<Integer> upper, List<Integer> lower) {
        boardGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && (row == 0 || row == 2);
        });

        for (int i = 0; i < upper.size(); i++) {
            Integer cardId = upper.get(i);
            if (cardId != null) {
                boolean isFirstBuilding = isFirstBuilding(upper, i);
                addCardToGrid(cardId, 0, i + 1, 0, i, isFirstBuilding);
            }
        }

        for (int i = 0; i < lower.size(); i++) {
            Integer cardId = lower.get(i);
            if (cardId != null) {
                boolean isFirstBuilding = isFirstBuilding(lower, i);
                addCardToGrid(cardId, 2, i + 1, 1, i, isFirstBuilding);
            }
        }
    }

    private void addCardToGrid(int cardId, int visualRow, int visualCol, int logicalRow, int logicalCol, boolean isFirstBuilding) {
        Image img = GuiAssetManager.getCardImage(cardId);
        if (img == null) return;

        ImageView cardView = new ImageView(img);
        cardView.setPreserveRatio(false);
        cardView.setSmooth(true);

        // Identico binding per le carte normali
        cardView.fitWidthProperty().bind(cardWidthProp);
        cardView.fitHeightProperty().bind(cardWidthProp.multiply(1.62));

        StackPane container = new StackPane(cardView);

        container.setUserData(cardId);

        // Stessi vincoli di blocco del contenitore
        container.minWidthProperty().bind(cardWidthProp);
        container.maxWidthProperty().bind(cardWidthProp);
        container.prefWidthProperty().bind(cardWidthProp);

        container.minHeightProperty().bind(cardWidthProp.multiply(1.62));
        container.maxHeightProperty().bind(cardWidthProp.multiply(1.62));
        container.prefHeightProperty().bind(cardWidthProp.multiply(1.62));

        container.setOnMouseClicked(e -> {
            if (!isEvent(cardId)) {
                handleCardClick(logicalRow, logicalCol);
            }
        });
        boardGrid.add(container, visualCol, visualRow);
    }

    public void enableCardSelection(boolean upperAllowed, boolean lowerAllowed, PlayerSnapshot myPlayer) {
        this.canPickUpper = upperAllowed;
        this.canPickLower = lowerAllowed;
        Platform.runLater(() -> {
            for (Node node : boardGrid.getChildren()) {
                Integer row = GridPane.getRowIndex(node);
                if (row == null) continue;

                node.getStyleClass().removeAll("card-glow-green", "card-glow-red");
                node.setCursor(Cursor.DEFAULT);

                boolean isEvent = false;
                boolean canAfford = true;

                if (node.getUserData() instanceof Integer cardId) {
                    isEvent = isEvent(cardId);
                    canAfford = isAffordable(cardId, myPlayer);
                }

                // Illumina solo se la riga è permessa E la carta non è un evento
                if (((row == 0 && upperAllowed) || (row == 2 && lowerAllowed)) && !isEvent) {
                    if (canAfford) {
                        node.getStyleClass().add("card-glow-green");
                        node.setCursor(Cursor.HAND);
                    } else {
                    // Non hai abbastanza cibo -> ombra rossa
                        node.getStyleClass().add("card-glow-red");
                        node.setCursor(Cursor.DEFAULT);
                    }
                }
                // Altrimenti spegne eventuali illuminazioni residue
                else if (row == 0 || row == 2) {
                    node.getStyleClass().removeAll("card-glow-green", "card-glow-red");
                    node.setCursor(Cursor.DEFAULT);
                }
            }
        });
    }

    /**
     * Disabilita ogni interazione visiva e rimuove gli effetti di illuminazione
     * sia dal tracciato dei totem che dalle carte.
     */
    public void disableAllInteractions() {
        this.canPickUpper = false;
        this.canPickLower = false;
        this.validTotemTiles = null;

        Platform.runLater(() -> {
            // Pulisce il tracciato e le relative ombre
            if (trackOverlays != null) {
                clearTrackHighlights(); // Usiamo il metodo appena sistemato per non duplicare codice
            }

            // Pulisce le carte
            for (Node node : boardGrid.getChildren()) {
                Integer row = GridPane.getRowIndex(node);
                if (row != null && (row == 0 || row == 2)) {
                    node.getStyleClass().removeAll("card-glow-green", "card-glow-red");
                    node.setEffect(null);
                    node.setCursor(Cursor.DEFAULT);
                }
            }
        });
    }

    public void enableTotemSelection(List<Integer> availableTiles) {
        this.validTotemTiles = availableTiles;
    }

    private void handleCardClick(int row, int col) {
        if (parentScreen == null) return;
        if (row == 0 && !canPickUpper) return;
        if (row == 1 && !canPickLower) return;

        parentScreen.onCardSelected(row, col);
    }

    private void renderTurnOrderTotems(GameModel model) {
        trackOverlays.forEach(p -> p.getChildren().clear());

        Map<String, PlayerSnapshot> players = model.getPlayers();
        Map<String, Integer> totemPositions = model.getTotemPositions();
        Map<String, Integer> returnPositions = model.getReturnPositions();
        double[] ySteps = getTurnOrderTotemYSteps(players.size());

        // DEDUZIONE ROUND: Stessa logica usata nell'interazione
        //boolean isRoundOne = model.getTribes().values().stream().allMatch(List::isEmpty);

        int fallbackYIndex = 0;

        for (PlayerSnapshot p : players.values()) {
            String nickname = p.getNickname();
            Integer offerPos = totemPositions.get(nickname);
            Integer retPos = returnPositions.get(nickname);

            // ROUND 1: Il totem resta invisibile finché non viene piazzato sull'offerta

            ImageView totemView = createTotemImageView(p.getTotemColor());
            if (totemView == null) continue;

            if (offerPos != null) {
                // È posizionato sull'Offertrack
                int visualCol = offerPos + 1;
                if (visualCol < trackOverlays.size()) {
                    Pane overlay = trackOverlays.get(visualCol);
                    totemView.layoutYProperty().bind(overlay.heightProperty().multiply(0.18));
                    overlay.getChildren().add(totemView);
                }
            } else {
                // È posizionato sulla Turn Order Tile (Base)
                Pane overlay = trackOverlays.get(0);

                // Usa l'ordine di ritorno se disponibile, altrimenti li impila progressivamente
                int yIndex = (retPos != null) ? retPos : fallbackYIndex++;

                if (yIndex >= 0 && yIndex < ySteps.length) {
                    totemView.layoutYProperty().bind(overlay.heightProperty().multiply(ySteps[yIndex]));
                    overlay.getChildren().add(totemView);
                }
            }
        }
    }

    private boolean isFirstBuilding(List<Integer> row, int index) {
        Integer currentId = row.get(index);
        if (currentId == null || !"Building".equals(it.polimi.ingsw.common.config.CardRegistry.getCard(currentId).type())) {
            return false;
        }
        // Controlla a ritroso se la carta precedente non era un edificio
        for (int i = index - 1; i >= 0; i--) {
            Integer prevId = row.get(i);
            if (prevId != null) {
                return !"Building".equals(it.polimi.ingsw.common.config.CardRegistry.getCard(prevId).type());
            }
        }
        return true;
    }

    // Metodo di utility per mantenere i binding di ridimensionamento coerenti
    private ImageView createTotemImageView(TotemColor color) {
        Image img = GuiAssetManager.getTotemImage(color);
        if (img == null) return null;

        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind(cardWidthProp.multiply(0.38));
        iv.layoutXProperty().bind(cardWidthProp.subtract(iv.fitWidthProperty()).divide(2));

        DropShadow borderShadow = new DropShadow();
        borderShadow.setColor(Color.rgb(20, 20, 20, 0.85)); // Quasi nero, molto opaco
        borderShadow.setRadius(4);
        borderShadow.setSpread(0.4); // Spread alto indurisce l'ombra trasformandola in contorno
        borderShadow.setOffsetY(2);  // Leggero spostamento in basso per l'effetto 3D
        iv.setEffect(borderShadow);

        return iv;
    }

    private double[] getTurnOrderTotemYSteps(int playerCount) {
        return switch (playerCount) {
            case 2 -> new double[]{0.23, 0.40};
            case 3 -> new double[]{0.16, 0.35, 0.54};
            case 4 -> new double[]{0.12, 0.32, 0.50, 0.68};
            default -> new double[]{0.06, 0.24, 0.42, 0.61, 0.80};
        };
    }

    public void highlightTotemPlacement(List<Integer> validIndices, String myNickname, GameModel model) {
        Map<String, Integer> totemPositions = model.getTotemPositions();

        Platform.runLater(() -> {
            boolean iAmOnOffer = totemPositions.containsKey(myNickname);

            // Se non siamo sulle offerte (quindi dobbiamo prelevare il totem dalla base)
            if (!iAmOnOffer) {
                if (trackOverlays.isEmpty()) return;

                Pane turnOrder = trackOverlays.get(0);
                turnOrder.setMouseTransparent(false);
                turnOrder.setCursor(Cursor.HAND);

                if (!turnOrder.getStyleClass().contains("tile-glow-white")) {
                    turnOrder.getStyleClass().add("tile-glow-white");
                }

                turnOrder.setOnMouseClicked(e -> {
                    turnOrder.getStyleClass().remove("tile-glow-white");
                    turnOrder.setMouseTransparent(true);
                    turnOrder.setOnMouseClicked(null);

                    showOfferTileOptions(validIndices);
                });
            }
        });
    }

    private void showOfferTileOptions(List<Integer> validIndices) {

        for (Integer idx : validIndices) {

            // Verifica come il server mappa le tessere offerta.
            // Se 1 è la prima Offer Tile, usa 'idx'. Se 0 è la prima Offer Tile, usa 'idx + 1'.
            int visualCol = idx + 1;

            if (visualCol >= trackOverlays.size()) continue;

            Pane p = trackOverlays.get(visualCol);
            p.setMouseTransparent(false);
            p.setCursor(Cursor.HAND);

            if (!p.getStyleClass().contains("tile-glow-yellow")) {
                p.getStyleClass().add("tile-glow-yellow");
            }

            p.setOnMouseClicked(e -> {
                clearTrackHighlights();
                parentScreen.onTotemPositionSelected(idx);
            });
        }
    }

    public void clearTrackHighlights() {
        for (Pane p : trackOverlays) {
            // Rimuove entrambe le possibili classi di illuminazione
            p.getStyleClass().removeAll("tile-glow-white", "tile-glow-yellow");

            p.setMouseTransparent(true);
            p.setCursor(Cursor.DEFAULT);
            p.setOnMouseClicked(null);
        }
    }

    private boolean isAffordable(int cardId, PlayerSnapshot player) {
        Integer baseCost = CardRegistry.getCard(cardId).foodCost();

        if (baseCost == null || baseCost == 0) {
            return true;
        }

        int finalCost = Math.max(baseCost - player.getFoodDiscount(), 0);
        boolean ris = player.getFood() >= finalCost;
        return player.getFood() >= finalCost;
    }

    private boolean isEvent(int cardId) {
        return "Event".equals(CardRegistry.getCard(cardId).type());
    }
}
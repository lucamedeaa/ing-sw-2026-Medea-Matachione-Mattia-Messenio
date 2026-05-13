package it.polimi.ingsw.client.view.gui.screen;


import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.gui.controllers.*;
import it.polimi.ingsw.client.view.gui.interaction.InteractionState;
import it.polimi.ingsw.client.view.listeners.InGameView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

import java.util.List;


public class InGameScreen implements InGameView, RefreshableScreen {

private final GuiContext ctx;      // Sostituisce controller, ctx.gameModel(), ctx.session() e ctx.notificationController()
    private final GuiNavigator navigator;
    private String viewedPlayerNickname;
    private boolean isNavigatingAway = false;

    @FXML private BorderPane rootPane;
    @FXML private BoardPanelController boardPanelController;
    @FXML private PlayersPanelController playersPanelController;
    @FXML private ActionsPanelController actionsPanelController;
    @FXML private LogPanelController logPanelController;
    @FXML private TribePanelController tribePanelController;


    public InGameScreen(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        // Usa il contesto per registrarti come listener
        ctx.notificationController().setInGameView(this);

        // Configura i sotto-pannelli usando i dati del contesto
        if (actionsPanelController != null) {
            actionsPanelController.setContext(ctx);
            actionsPanelController.setParentScreen(this);
        }
        if (boardPanelController != null) {
            boardPanelController.setParentScreen(this);
        }
        if (playersPanelController != null) {
            playersPanelController.setParentScreen(this);
        }
        if (tribePanelController != null) {
            tribePanelController.init(ctx.gameModel(), ctx.session());
        }

        this.viewedPlayerNickname = ctx.session().getNickname();

        Platform.runLater(() -> {
            // Controlla che la scena sia effettivamente montata per evitare crash
            if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) rootPane.getScene().getWindow();
                setupWindowConstraints(stage);
            }
            this.refresh();
        });
    }

    private void setupWindowConstraints(javafx.stage.Stage stage) {
        // Imposta il limite minimo della finestra di gioco
        stage.setMinWidth(1000);
        stage.setMinHeight(800);
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;

        ctx.notificationController().setInGameView(null);
        Platform.runLater(() -> navigator.toMatchmaking());
    }

    @Override
    public void onError(String error) {
        Platform.runLater(() -> {
            if (logPanelController != null) {
                logPanelController.appendError(error);
            }
        });
    }

    @Override
    public void onServerDisconnected(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;

        ctx.notificationController().setInGameView(null);
        Platform.runLater(() -> navigator.toDisconnected(reason));
    }

    @Override
    public void refresh() {
        if (isNavigatingAway) return;
        Platform.runLater(() -> {

            // Guardia contro il rendering prima del fullSync
            if (ctx.gameModel() == null || ctx.gameModel().getPlayers().isEmpty()) {
                return;
            }

            // Gestione fine partita con blocco anti-doppia-navigazione
            if (ctx.gameModel().isGameOver()) {
                isNavigatingAway = true;
                ctx.notificationController().setInGameView(null);
                navigator.toGameEnded();
                return;
            }

            // Delega il rendering ai sotto-pannelli per evitare una God Class
            if (boardPanelController != null) {
                boardPanelController.refresh(ctx.gameModel(), viewedPlayerNickname);
            }

            if (playersPanelController != null) {
                playersPanelController.refresh(ctx.gameModel(), ctx.session().getNickname());
            }
            if (actionsPanelController != null)
                actionsPanelController.refresh(ctx.gameModel(), ctx.session().getNickname());
            if (logPanelController != null) logPanelController.refresh(ctx.gameModel());
            if (tribePanelController != null) tribePanelController.refresh(ctx.gameModel());
        });
    }

    private InteractionState currentState = InteractionState.IDLE;

    // Variabili per mantenere il contesto dell'azione in corso
    private int upperPicksAllowed = 0;
    private int lowerPicksAllowed = 0;

    // --- Metodi richiamati dall' ActionsPanelController ---

    public void startTakeCardFlow(int upperPicks, int lowerPicks) {
        this.currentState = InteractionState.SELECTING_CARD_TO_TAKE;
        this.upperPicksAllowed = upperPicks;
        this.lowerPicksAllowed = lowerPicks;

        // Ordina al tabellone di rendere le carte cliccabili
        if (boardPanelController != null) {
            boardPanelController.enableCardSelection(upperPicks > 0, lowerPicks > 0);
        }
    }

    public void startPlaceTotemFlow(List<Integer> availableTiles) {
        this.currentState = InteractionState.SELECTING_TOTEM_POSITION;
        if (boardPanelController != null) {
            boardPanelController.enableTotemSelection(availableTiles);
        }
    }

    // --- Metodi richiamati dal BoardPanelController a seguito del click visivo ---

    public void onCardSelected(int row, int col) {
        if (currentState != InteractionState.SELECTING_CARD_TO_TAKE) return;

        // Validazione lato client per evitare di mandare pacchetti inutili
        if (row == 0 && upperPicksAllowed <= 0) return;
        if (row == 1 && lowerPicksAllowed <= 0) return;

        // Invia il comando al server
        ctx.controller().takeCard(row, col);

        // Reset dello stato
        resetInteraction();
    }

    public void onTotemPositionSelected(int tileIndex) {
        if (currentState != InteractionState.SELECTING_TOTEM_POSITION) return;

        ctx.controller().placeTotem(tileIndex);
        resetInteraction();
    }

    private void resetInteraction() {
        this.currentState = InteractionState.IDLE;
        if (boardPanelController != null) {
            boardPanelController.disableSelection(); // Blocca i click visivi
        }
    }
    public String getViewedPlayer() {
        return viewedPlayerNickname;
    }

    // Metodo chiamato dal PlayersPanelController al click
    public void setViewedPlayer(String nickname) {
        this.viewedPlayerNickname = nickname;
        this.refresh(); // Aggiorna tutto per mostrare i dati del nuovo giocatore
    }

    public void promptTotemPlacement(List<Integer> availableTiles) {
        this.currentState = InteractionState.SELECTING_TOTEM_POSITION;
        String self = ctx.session().getNickname();
        GameModel model = ctx.gameModel();

        if (boardPanelController != null && model != null) {
            // Passiamo nickname e model per verificare se siamo già sulla Turn Order Tile
            boardPanelController.highlightTotemPlacement(availableTiles, self, model);
        }
    }

}
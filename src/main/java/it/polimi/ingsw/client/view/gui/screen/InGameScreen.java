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
import javafx.scene.layout.StackPane;

import java.util.List;

public class InGameScreen implements InGameView, RefreshableScreen {

    private final GuiContext ctx;
    private final GuiNavigator navigator;
    private String viewedPlayerNickname;
    private boolean isNavigatingAway = false;

    // LA FIX È QUI: Usa StackPane invece di BorderPane!
    @FXML private StackPane rootPane;

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
        ctx.notificationController().setInGameView(this);

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
            if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) rootPane.getScene().getWindow();
                stage.setMinWidth(1000);
                stage.setMinHeight(800);
            }
            this.refresh();
        });
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        if (isNavigatingAway) return;
        isNavigatingAway = true;
        ctx.notificationController().setInGameView(null);
        Platform.runLater(navigator::toMatchmaking);
    }

    @Override
    public void onError(String error) {
        Platform.runLater(() -> {
            if (logPanelController != null) logPanelController.appendError(error);
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
            if (ctx.gameModel() == null || ctx.gameModel().getPlayers().isEmpty()) return;

            if (ctx.gameModel().isGameOver()) {
                isNavigatingAway = true;
                ctx.notificationController().setInGameView(null);
                navigator.toGameEnded();
                return;
            }

            if (boardPanelController != null) boardPanelController.refresh(ctx.gameModel(), viewedPlayerNickname);
            if (playersPanelController != null) playersPanelController.refresh(ctx.gameModel(), ctx.session().getNickname());
            if (actionsPanelController != null) actionsPanelController.refresh(ctx.gameModel(), ctx.session().getNickname());
            if (logPanelController != null) logPanelController.refresh(ctx.gameModel());
            if (tribePanelController != null) tribePanelController.refresh(ctx.gameModel());
        });
    }

    private InteractionState currentState = InteractionState.IDLE;
    private int upperPicksAllowed = 0;
    private int lowerPicksAllowed = 0;

    public void startTakeCardFlow(int upperPicks, int lowerPicks) {
        this.currentState = InteractionState.SELECTING_CARD_TO_TAKE;
        this.upperPicksAllowed = upperPicks;
        this.lowerPicksAllowed = lowerPicks;
        if (boardPanelController != null) boardPanelController.enableCardSelection(upperPicks > 0, lowerPicks > 0);
    }

    public void startPlaceTotemFlow(List<Integer> availableTiles) {
        this.currentState = InteractionState.SELECTING_TOTEM_POSITION;
        if (boardPanelController != null) boardPanelController.enableTotemSelection(availableTiles);
    }

    public void onTotemPositionSelected(int tileIndex) {
        if (currentState != InteractionState.SELECTING_TOTEM_POSITION) return;
        ctx.controller().placeTotem(tileIndex);
        resetInteraction();
    }

    private void resetInteraction() {
        this.currentState = InteractionState.IDLE;
        if (boardPanelController != null) boardPanelController.disableAllInteractions();
    }

    public String getViewedPlayer() { return viewedPlayerNickname; }

    public void setViewedPlayer(String nickname) {
        this.viewedPlayerNickname = nickname;
        this.refresh();
    }

    public void promptTotemPlacement(List<Integer> availableTiles) {
        this.currentState = InteractionState.SELECTING_TOTEM_POSITION;
        String self = ctx.session().getNickname();
        GameModel model = ctx.gameModel();
        if (boardPanelController != null && model != null) {
            boardPanelController.highlightTotemPlacement(availableTiles, self, model);
        }
    }

    public void onCardSelected(int row, int col) {
        if (currentState != InteractionState.SELECTING_CARD_TO_TAKE) return;
        if (row == 0 && upperPicksAllowed <= 0) return;
        if (row == 1 && lowerPicksAllowed <= 0) return;

        ctx.controller().takeCard(row, col);
        if (boardPanelController != null) boardPanelController.disableAllInteractions();
        resetInteraction();
    }

    public void promptCardSelection(int upperPicksAllowed, int lowerPicksAllowed) {
        this.currentState = InteractionState.SELECTING_CARD_TO_TAKE;
        this.upperPicksAllowed = upperPicksAllowed;
        this.lowerPicksAllowed = lowerPicksAllowed;
        if (boardPanelController != null) {
            boardPanelController.enableCardSelection(upperPicksAllowed > 0, lowerPicksAllowed > 0);
        }
    }
}
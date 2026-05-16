package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.controllers.*;
import it.polimi.ingsw.client.view.gui.controllers.board.BoardSelectionListener;
import it.polimi.ingsw.client.view.gui.interaction.InteractionState;
import it.polimi.ingsw.client.view.gui.presenter.InGamePresenter;
import it.polimi.ingsw.client.view.gui.viewstate.GameViewState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import java.util.List;
import java.util.Set;

public class InGameScreen implements RefreshableScreen, BoardSelectionListener,InGamePanelHost {

    private final InGamePresenter presenter;
    private final GuiContext ctx;

    @FXML private StackPane rootPane;
    @FXML private BoardPanelController boardPanelController;
    @FXML private PlayersPanelController playersPanelController;
    @FXML private ActionsPanelController actionsPanelController;
    @FXML private LogPanelController logPanelController;
    @FXML private TribePanelController tribePanelController;
    @FXML private StackPane logOverlay;

    private String viewedPlayerNickname;
    private InteractionState currentState = InteractionState.IDLE;
    private int upperPicksAllowed = 0;
    private int lowerPicksAllowed = 0;

    public InGameScreen(InGamePresenter presenter, GuiContext ctx) {
        this.presenter = presenter;
        this.ctx = ctx;
    }

    @FXML
    public void initialize() {
        if (actionsPanelController != null) {
            actionsPanelController.setParentScreen(this);
        }
        if (boardPanelController != null) boardPanelController.setListener(this);
        if (playersPanelController != null) playersPanelController.setParentScreen(this);

        Platform.runLater(() -> {
            if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) rootPane.getScene().getWindow();
                stage.setMinWidth(1000);
                stage.setMinHeight(800);
            }
        });
    }


    /** Esegue il rendering dei sotto-pannelli. Deve essere chiamato sul thread JavaFX. */
    public void doRefresh(GameViewState state) {
            if (!state.actions().isMyTurn()) {
                currentState = InteractionState.IDLE;
                if (boardPanelController != null) boardPanelController.disableAllInteractions();
            }
        if (viewedPlayerNickname == null) viewedPlayerNickname = state.selfNickname();
        if (boardPanelController   != null) boardPanelController.render(state.board());
        if (playersPanelController != null) playersPanelController.render(
                state.players(), state.selfNickname(), state.activePlayer(), viewedPlayerNickname);
        if (actionsPanelController != null) actionsPanelController.render(state.actions());
        if (logPanelController     != null) logPanelController.render(state.newLogs());
        if (tribePanelController   != null) tribePanelController.render(
                state.tribes().getOrDefault(viewedPlayerNickname, List.of()), viewedPlayerNickname);
    }
    public void showError(String error) {
        if (logPanelController != null) logPanelController.appendError(error);
    }

    public void toggleLog() {
        if (logOverlay != null) logOverlay.setVisible(!logOverlay.isVisible());
    }

    @Override
    public void refresh() { presenter.refresh(); }

    @Override
    public void onEnter() { presenter.onScreenReady(this); }

    @Override
    public void onExit() {
        presenter.deregister();
    }

    @Override
    public void handleWindowClose(WindowEvent event, GuiContext ctx, GuiNavigator navigator) {
        presenter.handleWindowClose(event);
    }


    public void promptCardSelection(int upperPicksAllowed, int lowerPicksAllowed, Set<Integer> affordableIds, Set<Integer> unaffordableIds) {
        this.currentState = InteractionState.SELECTING_CARD_TO_TAKE;
        this.upperPicksAllowed = upperPicksAllowed;
        this.lowerPicksAllowed = lowerPicksAllowed;
        if (boardPanelController != null)
            boardPanelController.enableCardSelection(
                    upperPicksAllowed > 0, lowerPicksAllowed > 0,
                    affordableIds, unaffordableIds
            );
    }

    public void onCardSelected(int row, int col) {
        if (currentState != InteractionState.SELECTING_CARD_TO_TAKE) return;
        if (row == 0 && upperPicksAllowed <= 0) return;
        if (row == 1 && lowerPicksAllowed <= 0) return;
        ctx.controller().takeCard(row, col);
        resetInteraction();
    }

    public void promptTotemPlacement(List<Integer> availableTiles) {
        this.currentState = InteractionState.SELECTING_TOTEM_POSITION;
        GameModel model = ctx.gameModel();
        if (boardPanelController != null && model != null) {
            boolean iAmOnOffer = model.getTotemPositions()
                    .containsKey(ctx.session().getNickname());
            boardPanelController.highlightTotemPlacement(availableTiles, iAmOnOffer);
        }
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
        refresh();
    }

    public void disconnect() {
        presenter.disconnect();
    }
    public void skipAction() { presenter.skipAction(); }
    public void leaveGame()  { presenter.leave(); }
}
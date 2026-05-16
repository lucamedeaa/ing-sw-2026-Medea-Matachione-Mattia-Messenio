package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.gui.controllers.*;
import it.polimi.ingsw.client.view.gui.controllers.board.BoardSelectionListener;
import it.polimi.ingsw.client.view.gui.interaction.InteractionState;
import it.polimi.ingsw.client.view.gui.presenter.InGamePresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;

import java.util.List;

public class InGameScreen implements RefreshableScreen, BoardSelectionListener {

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
        presenter.onScreenReady(this);
    }


    /** Esegue il rendering dei sotto-pannelli. Deve essere chiamato sul thread JavaFX. */
    public void doRefresh() {
        String self = ctx.session().getNickname();
        if (viewedPlayerNickname == null) viewedPlayerNickname = self;
        GameModel model = ctx.gameModel();

        if (boardPanelController != null)   boardPanelController.refresh(model, viewedPlayerNickname);
        if (playersPanelController != null) playersPanelController.refresh(model, self);
        if (actionsPanelController != null) actionsPanelController.refresh(model, self);
        if (logPanelController != null)     logPanelController.refresh(model);
        if (tribePanelController != null)   tribePanelController.refresh(model, viewedPlayerNickname);
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
    public void handleWindowClose(WindowEvent event, GuiContext ctx, GuiNavigator navigator) {
        presenter.handleWindowClose(event);
    }


    public void promptCardSelection(int upperPicksAllowed, int lowerPicksAllowed) {
        this.currentState = InteractionState.SELECTING_CARD_TO_TAKE;
        this.upperPicksAllowed = upperPicksAllowed;
        this.lowerPicksAllowed = lowerPicksAllowed;
        PlayerSnapshot me = ctx.gameModel().getPlayers().get(ctx.session().getNickname());
        if (boardPanelController != null)
            boardPanelController.enableCardSelection(upperPicksAllowed > 0, lowerPicksAllowed > 0, me);
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
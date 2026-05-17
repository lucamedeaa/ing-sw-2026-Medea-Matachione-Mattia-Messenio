package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.Scenes;
import it.polimi.ingsw.client.view.gui.controllers.*;
import it.polimi.ingsw.client.view.gui.controllers.board.BoardSelectionListener;
import it.polimi.ingsw.client.view.gui.interaction.BoardInteractionManager;
import it.polimi.ingsw.client.view.gui.presenter.InGamePresenter;
import it.polimi.ingsw.client.view.gui.presenter.InGameScreenPort;
import it.polimi.ingsw.client.view.gui.viewstate.GameViewState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import java.util.List;
import java.util.Set;

public class InGameScreen implements RefreshableScreen, BoardSelectionListener, InGameScreenPort, ViewedPlayerHost, ActionCommandHost {

    private final InGamePresenter presenter;
    private final ModalOpener modalOpener;
    private GameViewState lastState;

    @FXML private StackPane rootPane;
    @FXML private BoardPanelController boardPanelController;
    @FXML private PlayersPanelController playersPanelController;
    @FXML private ActionsPanelController actionsPanelController;
    @FXML private LogPanelController logPanelController;
    @FXML private TribePanelController tribePanelController;
    @FXML private StackPane logOverlay;

    private String viewedPlayerNickname;
    private BoardInteractionManager interactionManager;

    public InGameScreen(InGamePresenter presenter, ModalOpener modalOpener) {
        this.presenter = presenter;
        this.modalOpener = modalOpener;
    }

    @FXML
    public void initialize() {
        if (boardPanelController != null) {
            boardPanelController.setListener(this);
            interactionManager = new BoardInteractionManager(presenter, boardPanelController);
        }
        if (actionsPanelController != null) actionsPanelController.setParentScreen(this);
        if (playersPanelController != null) playersPanelController.setParentScreen(this);

        Platform.runLater(() -> {
            if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) rootPane.getScene().getWindow();
                stage.setMinWidth(1000);
                stage.setMinHeight(800);
            }
        });
    }

    @Override
    public void doRefresh(GameViewState state) {
        this.lastState = state;
        if (!state.actions().isMyTurn()) interactionManager.reset();
        if (viewedPlayerNickname == null) viewedPlayerNickname = state.selfNickname();
        if (boardPanelController   != null) boardPanelController.render(state.board());
        if (playersPanelController != null) playersPanelController.render(
                state.players(), state.selfNickname(), state.activePlayer(), viewedPlayerNickname);
        if (actionsPanelController != null) actionsPanelController.render(state.actions());
        if (logPanelController     != null) logPanelController.render(state.newLogs());
        if (tribePanelController   != null) tribePanelController.render(
                state.tribes().getOrDefault(viewedPlayerNickname, List.of()), viewedPlayerNickname);
    }

    @Override public void showError(String error)          { if (logPanelController != null) logPanelController.appendError(error); }
    @Override public void refresh()                        { presenter.refresh(); }
    @Override public void onEnter()                        { presenter.onScreenReady(this); }
    @Override public void onExit()                         { presenter.deregister(); }
    @Override public void handleWindowClose(WindowEvent e) { presenter.handleWindowClose(e); }

    @Override public void onCardSelected(int row, int col) { interactionManager.onCardSelected(row, col); }
    @Override public void onTotemPositionSelected(int idx) { interactionManager.onTotemPositionSelected(idx); }

    @Override
    public void promptCardSelection(int upper, int lower, Set<Integer> affordableIds, Set<Integer> unaffordableIds) {
        interactionManager.promptCardSelection(upper, lower, affordableIds, unaffordableIds);
    }
    @Override
    public void promptTotemPlacement(List<Integer> availableTiles) {
        if (lastState == null) return;
        boolean iAmOnOffer = lastState.board().totemPositions().containsKey(lastState.selfNickname());
        interactionManager.promptTotemPlacement(availableTiles, iAmOnOffer);
    }
    @Override public void skipAction() { presenter.skipAction(); }
    @Override public void leaveGame()  { presenter.leave(); }
    @Override public void disconnect() { presenter.disconnect(); }
    @Override public void toggleLog()  { if (logOverlay != null) logOverlay.setVisible(!logOverlay.isVisible()); }
    @Override public void showInfo()   { modalOpener.openModal(Scenes.INFO); }

    @Override
    public void setViewedPlayer(String nickname) {
        this.viewedPlayerNickname = nickname;
        refresh();
    }
}
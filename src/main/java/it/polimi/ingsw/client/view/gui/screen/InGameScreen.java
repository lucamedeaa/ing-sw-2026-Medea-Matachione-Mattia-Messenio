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

/**
 * FXML controller for the main in-game screen.
 */
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

    /**
     * Creates an in-game screen.
     *
     * @param presenter presenter that drives the screen
     * @param modalOpener modal opener used for auxiliary screens
     */
    public InGameScreen(InGamePresenter presenter, ModalOpener modalOpener) {
        this.presenter = presenter;
        this.modalOpener = modalOpener;
    }

    /**
     * Wires nested controllers and initializes stage constraints.
     */
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

    /** {@inheritDoc} */
    @Override
    public void doRefresh(GameViewState state) {
        this.lastState = state;
        if (!state.actions().isMyTurn()) interactionManager.reset();
        if (viewedPlayerNickname == null) viewedPlayerNickname = state.selfNickname();
        if (boardPanelController   != null) boardPanelController.render(state.board());
        if (playersPanelController != null) playersPanelController.render(
                state.players(), state.selfNickname(), state.activePlayer(), viewedPlayerNickname, state.board().round(), state.board().currentEra());
        if (actionsPanelController != null) actionsPanelController.render(state.actions());
        if (logPanelController     != null) logPanelController.render(state.newLogs());
        if (tribePanelController   != null) tribePanelController.render(
                state.tribes().getOrDefault(viewedPlayerNickname, List.of()), viewedPlayerNickname);
    }

    /** {@inheritDoc} */
    @Override public void showError(String error)          { if (logPanelController != null) logPanelController.appendError(error); }

    /** {@inheritDoc} */
    @Override public void refresh()                        { presenter.refresh(); }

    /** {@inheritDoc} */
    @Override public void onEnter()                        { presenter.onScreenReady(this); }

    /** {@inheritDoc} */
    @Override public void onExit()                         { presenter.deregister(); }

    /** {@inheritDoc} */
    @Override public void handleWindowClose(WindowEvent e) { presenter.handleWindowClose(e); }

    /** {@inheritDoc} */
    @Override public void onCardSelected(int row, int col) { interactionManager.onCardSelected(row, col); }

    /** {@inheritDoc} */
    @Override public void onTotemPositionSelected(int idx) { interactionManager.onTotemPositionSelected(idx); }

    /** {@inheritDoc} */
    @Override
    public void promptCardSelection(int upper, int lower, Set<Integer> affordableIds, Set<Integer> unaffordableIds) {
        interactionManager.promptCardSelection(upper, lower, affordableIds, unaffordableIds);
    }

    /** {@inheritDoc} */
    @Override
    public void promptTotemPlacement(List<Integer> availableTiles) {
        if (lastState == null) return;
        boolean iAmOnOffer = lastState.board().totemPositions().containsKey(lastState.selfNickname()); // "on offer" = my totem is already on the offer track
        interactionManager.promptTotemPlacement(availableTiles, iAmOnOffer);
    }

    /** {@inheritDoc} */
    @Override public void skipAction() { presenter.skipAction(); }

    /** {@inheritDoc} */
    @Override public void leaveGame()  { presenter.leave(); }

    /** {@inheritDoc} */
    @Override public void disconnect() { presenter.disconnect(); }

    /** {@inheritDoc} */
    @Override public void toggleLog()  { if (logOverlay != null) logOverlay.setVisible(!logOverlay.isVisible()); }

    /** {@inheritDoc} */
    @Override public void showInfo()   { modalOpener.openModal(Scenes.INFO); }

    /** {@inheritDoc} */
    @Override
    public void setViewedPlayer(String nickname) {
        this.viewedPlayerNickname = nickname;
        refresh();
    }
}

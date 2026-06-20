package it.polimi.ingsw.client.view.tui;

import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.command.ServerCommandPort;
import it.polimi.ingsw.client.view.tui.state.*;

/**
 * Creates TUI states and installs them in the active state container.
 */
public class TuiRouter implements TuiNavigator {
    private final StateContainer container;
    private final LobbyModel lobbyModel;
    private final GameModel gameModel;
    private final ClientSession session;
    private final OutputPort out;
    private ClientNotificationController notificationController;
    private ServerCommandPort controller;
    private final ApplicationLifecyclePort lifecyclePort;

    /**
     * Builds a router with the shared models and ports required by the TUI states.
     *
     * @param container active state container
     * @param lobbyModel client-side lobby model
     * @param gameModel client-side game model
     * @param session client session data
     * @param out output port used by rendered states
     * @param notificationController controller that dispatches server notifications to views
     * @param lifecyclePort lifecycle port used for shutdown requests
     */
    public TuiRouter(StateContainer container, LobbyModel lobbyModel, GameModel gameModel,
                     ClientSession session, OutputPort out, ClientNotificationController notificationController, ApplicationLifecyclePort lifecyclePort) {
        this.container = container;
        this.lobbyModel = lobbyModel;
        this.gameModel = gameModel;
        this.session = session;
        this.out = out;
        this.notificationController = notificationController;
        this.lifecyclePort = lifecyclePort;
    }

    /**
     * Updates the notification controller used by subsequently created states.
     *
     * @param nc notification controller to use
     */
    public void setNotificationController(ClientNotificationController nc) {
        this.notificationController = nc;
    }

    /**
     * Updates the server command port used by subsequently created states.
     *
     * @param controller server command port to use
     */
    public void setController(ServerCommandPort controller) {
        this.controller = controller;
    }

    /** {@inheritDoc} */
    @Override
    public void toMatchmaking() {
        container.updateState(new MatchmakingUiState(this, lobbyModel, controller, session, out, notificationController, lifecyclePort));
    }

    /** {@inheritDoc} */
    @Override
    public void toLobby() {
        container.updateState(new LobbyUiState(this, lobbyModel,gameModel, controller, session, out, notificationController, lifecyclePort));
    }

    /** {@inheritDoc} */
    @Override
    public void toInGame() {
        container.updateState(new InGameUiState(this, gameModel, controller, session, out, notificationController, lifecyclePort));
    }

    /** {@inheritDoc} */
    @Override
    public void toViewTribe(String targetPlayer) {
        container.updateState(new ViewTribeUiState(this, gameModel, out, targetPlayer, notificationController));
    }

    /** {@inheritDoc} */
    @Override
    public void toInfo() {
        container.updateState(new InfoUiState(this, out, notificationController));
    }

    /** {@inheritDoc} */
    @Override
    public void toGameEnded() {
        container.updateState(new GameEndedUiState(this, gameModel, controller,session, out, notificationController, lifecyclePort));
    }

    /** {@inheritDoc} */
    @Override
    public void toDisconnected(String reason) {
        container.updateState(new DisconnectedUiState(out, reason,lifecyclePort));
    }
}

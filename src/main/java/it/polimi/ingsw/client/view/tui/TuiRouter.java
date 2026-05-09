package it.polimi.ingsw.client.view.tui;

import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.command.ServerCommandPort;
import it.polimi.ingsw.client.view.tui.state.*;

public class TuiRouter implements TuiNavigator {
    private final StateContainer container;
    private final LobbyModel lobbyModel;
    private final GameModel gameModel;
    private final ClientSession session;
    private final OutputPort out;
    private ClientNotificationController notificationController;
    private ServerCommandPort controller;

    public TuiRouter(StateContainer container, LobbyModel lobbyModel, GameModel gameModel,
                     ClientSession session, OutputPort out, ClientNotificationController notificationController) {
        this.container = container;
        this.lobbyModel = lobbyModel;
        this.gameModel = gameModel;
        this.session = session;
        this.out = out;
    }
    public void setNotificationController(ClientNotificationController nc) {
        this.notificationController = nc;
    }

    public void setController(ServerCommandPort controller) {
        this.controller = controller;
    }

    @Override
    public void toMatchmaking() {
        container.updateState(new MatchmakingUiState(this, lobbyModel, controller, session, out, notificationController));
    }

    @Override
    public void toLobby() {
        container.updateState(new LobbyUiState(this, lobbyModel, controller, session, out, notificationController));
    }

    @Override
    public void toInGame() {
        container.updateState(new InGameUiState(this, gameModel, controller, session, out, notificationController));
    }

    @Override
    public void toViewTribe(String targetPlayer) {
        container.updateState(new ViewTribeUiState(this, gameModel, out, targetPlayer));
    }

    @Override
    public void toInfo() {
        container.updateState(new InfoUiState(this, out));
    }

    @Override
    public void toGameEnded() {
        container.updateState(new GameEndedUiState(this, gameModel, controller, out, notificationController));
    }

    @Override
    public void toDisconnected(String reason) {
        container.updateState(new DisconnectedUiState(out, reason));
    }
}
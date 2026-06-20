package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.ApplicationLifecyclePort;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.command.CommandFactory;
import it.polimi.ingsw.client.view.tui.command.DisconnectCommand;
import it.polimi.ingsw.client.view.tui.command.LeaveGameCommand;
import it.polimi.ingsw.client.view.tui.command.ServerCommandPort;
import it.polimi.ingsw.client.view.tui.render.ColorAnsi;
import it.polimi.ingsw.client.view.tui.render.LobbyRenderer;
import it.polimi.ingsw.client.view.listeners.LobbyView;
import it.polimi.ingsw.server.model.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * State that renders a joined lobby and handles lobby-level commands.
 */
public class LobbyUiState implements UIState, LobbyView {
    private final TuiNavigator navigator;
    private final LobbyModel lobbyModel;
    private final GameModel gameModel;
    private final ServerCommandPort controller;
    private final ClientSession session;
    private final OutputPort out;
    private final ClientNotificationController notificationController;

    private final LobbyRenderer renderer;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();

    private final ApplicationLifecyclePort lifecyclePort;

    /**
     * Creates a lobby state.
     *
     * @param navigator navigator used for screen transitions
     * @param lobbyModel model containing lobby data
     * @param gameModel model used to detect early game synchronization
     * @param controller server command port
     * @param session local client session
     * @param out output port used for rendering and feedback
     * @param notificationController notification controller used to receive lobby events
     * @param lifecyclePort lifecycle port used by disconnect commands
     */
    public LobbyUiState(TuiNavigator navigator, LobbyModel lobbyModel, GameModel gameModel, ServerCommandPort controller, ClientSession session, OutputPort out, ClientNotificationController notificationController, ApplicationLifecyclePort lifecyclePort) {
        this.navigator = navigator;
        this.lobbyModel = lobbyModel;
        this.controller = controller;
        this.session = session;
        this.out = out;
        this.notificationController = notificationController;
        this.renderer = new LobbyRenderer(out);
        this.lifecyclePort = lifecyclePort;
        this.gameModel = gameModel;

        registerCommands();
    }


    /** {@inheritDoc} */
    @Override
    public void onEnter() {
        this.notificationController.setLobbyView(this);
        // If FullSync arrived before this view was registered, the GameModel is already populated
        // and the lobby must immediately transition to the in-game screen.
        if (!gameModel.getPlayers().isEmpty()) {
            onGameStarted();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onExit() {
        this.notificationController.setLobbyView(null);
    }

    private void registerCommands() {
        commandRegistry.put("0", args -> new LeaveGameCommand(controller, out));
        commandRegistry.put("d", args -> new DisconnectCommand(controller, lifecyclePort, out));
    }

    /** {@inheritDoc} */
    @Override
    public void render() {
        String error = lobbyModel.consumeGlobalError();


            renderer.render(
                    lobbyModel.getLobbyPlayers(),
                    lobbyModel.getLobbyNotification(),
                    session.getNickname()
            );

        if (error != null && !error.isEmpty()) {
            out.print(ColorAnsi.RED_BOLD + "\n[ERRORE SERVER]: " + error + ColorAnsi.RESET);
            out.prompt(ColorAnsi.YELLOW_BOLD + "\nUntil the whole tribe is here > " + ColorAnsi.RESET);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void handleInput(String input) {
        if (input == null || input.isBlank()) return;
        String[] parts = input.trim().split("\\s+");
        CommandFactory factory = commandRegistry.get(parts[0].toLowerCase());
        if (factory != null) factory.create(parts).execute();
    }

    /** {@inheritDoc} */
    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) {
        //render(); //gia fatto in chiusura del batch
    }

    /** {@inheritDoc} */
    @Override
    public void onError(String error) {
        // estisci l'errore in lobby
    }

    /** {@inheritDoc} */
    @Override
    public void onGameStarted() {
        notificationController.setLobbyView(null);
        navigator.toInGame();
    }

    /** {@inheritDoc} */
    @Override
    public void onReturnToMatchmaking(String reason) {
        navigator.toMatchmaking();
    }

    /** {@inheritDoc} */
    @Override
    public void onServerDisconnected(String reason) {
        navigator.toDisconnected(reason);
    }
}

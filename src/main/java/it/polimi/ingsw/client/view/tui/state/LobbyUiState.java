package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.command.CommandFactory;
import it.polimi.ingsw.client.view.tui.command.DisconnectCommand;
import it.polimi.ingsw.client.view.tui.command.LeaveGameCommand;
import it.polimi.ingsw.client.view.tui.command.ServerCommandPort;
import it.polimi.ingsw.client.view.tui.render.LobbyRenderer;
import it.polimi.ingsw.client.view.listeners.LobbyView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyUiState implements UIState, LobbyView {
    private final TuiNavigator navigator;
    private final LobbyModel lobbyModel;
    private final ServerCommandPort controller;
    private final ClientSession session;
    private final OutputPort out;
    private final ClientNotificationController notificationController;

    private final LobbyRenderer renderer;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();
    private boolean initialized = false;

    public LobbyUiState(TuiNavigator navigator, LobbyModel lobbyModel, ServerCommandPort controller, ClientSession session, OutputPort out, ClientNotificationController notificationController) {
        this.navigator = navigator;
        this.lobbyModel = lobbyModel;
        this.controller = controller;
        this.session = session;
        this.out = out;
        this.notificationController = notificationController;
        this.renderer = new LobbyRenderer(out);

        registerCommands();
        this.notificationController.setLobbyView(this);
    }

    private void registerCommands() {
        commandRegistry.put("0", args -> new LeaveGameCommand(controller, out));
        commandRegistry.put("d", args -> new DisconnectCommand(controller));
    }

    @Override
    public void render() {
        if(!initialized) { return; }

        lobbyModel.getReadLock().lock();
        try {
            renderer.render(
                    lobbyModel.getLobbyPlayers(),
                    lobbyModel.getLobbyNotification(),
                    session.getNickname()
            );
        } finally {
            lobbyModel.getReadLock().unlock();
        }
    }

    @Override
    public void handleInput(String input) {
        if (input == null || input.isBlank()) return;
        String[] parts = input.trim().split("\\s+");
        CommandFactory factory = commandRegistry.get(parts[0].toLowerCase());
        if (factory != null) factory.create(parts).execute();
    }

    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) {
        this.initialized = true;
        //render(); //gia fatto in chiusura del batch
    }

    @Override
    public void onError(String error) {
        // estisci l'errore in lobby
    }

    @Override
    public void onGameStarted() {
        //  Mi de-registro
        notificationController.setLobbyView(null);

        // va in gioco
        navigator.toInGame();
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        notificationController.setLobbyView(null);
        navigator.toMatchmaking();
    }

    @Override
    public void onServerDisconnected(String reason) {
        notificationController.setLobbyView(null);
        navigator.toLobby();
    }
}

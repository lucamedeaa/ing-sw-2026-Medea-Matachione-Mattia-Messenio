package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.commands.*;
import it.polimi.ingsw.client.tui.render.LobbyRenderer;
import it.polimi.ingsw.client.view.listeners.LobbyView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyState implements UIState, LobbyView {
    private final NavigationPort nav;
    private final OutputPort out;
    private final LobbyRenderer renderer;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();
    private boolean initialized = false;

    public LobbyState(NavigationPort nav, OutputPort out) {
        this.nav = nav;
        this.out = out;
        this.renderer = new LobbyRenderer(out);

        registerCommands();
        nav.getNotificationController().setLobbyView(this);
    }

    private void registerCommands() {
        commandRegistry.put("0", args -> new LeaveGameCommand(nav.getController(), out));
        commandRegistry.put("d", args -> new DisconnectCommand(nav.getController()));
    }

    @Override
    public void render() {
        if(!initialized) { return; }

        nav.getLobbyModel().getReadLock().lock();
        try {
            renderer.render(
                    nav.getLobbyModel().getLobbyPlayers(),
                    nav.getLobbyModel().getLobbyNotification(),
                    nav.getMyNickname()
            );
        } finally {
            nav.getLobbyModel().getReadLock().unlock();
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
        render(); // Aggiorno lo schermo con il nuovo giocatore
    }

    @Override
    public void onError(String error) {
        // estisci l'errore in lobby
    }

    @Override
    public void onGameStarted() {
        //  Mi de-registro
        nav.getNotificationController().setLobbyView(null);

        // va in gioco
        nav.changeState(new InGameState(nav, out));
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        nav.getNotificationController().setLobbyView(null);
        nav.changeState(new MatchmakingState(nav, out));
    }

    @Override
    public void onServerDisconnected(String reason) {
        nav.getNotificationController().setLobbyView(null);
        nav.changeState(new DisconnectedState(out, reason));
    }
}

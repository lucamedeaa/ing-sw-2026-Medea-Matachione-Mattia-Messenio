package it.polimi.ingsw.client.view.tui.state;

import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.client.view.tui.TuiNavigator;
import it.polimi.ingsw.client.view.tui.command.*;
import it.polimi.ingsw.client.view.tui.render.MatchmakingRenderer;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.common.network.dto.GameInfoDto;

import java.util.*;

import static java.util.Arrays.copyOfRange;

public class MatchmakingUiState implements UIState, MatchmakingView {
    private final TuiNavigator navigator;
    private final LobbyModel lobbyModel;
    private final ServerCommandPort controller;
    private final ClientSession session;
    private final OutputPort out;
    private final ClientNotificationController notificationController;

    private final MatchmakingRenderer renderer;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();
    private String pendingNickname = "";
    private boolean showGamesList = false;

    public MatchmakingUiState(TuiNavigator navigator, LobbyModel lobbyModel, ServerCommandPort controller, ClientSession session, OutputPort out,ClientNotificationController notificationController) {
        this.navigator = navigator;
        this.lobbyModel = lobbyModel;
        this.controller = controller;
        this.session = session;
        this.out = out;
        this.notificationController = notificationController;
        this.renderer = new MatchmakingRenderer(out);

        registerCommands();

        this.notificationController.setMatchmakingView(this);

        //render();
    }

    private void registerCommands() {
        commandRegistry.put("create", args -> {
            if (args.length < 3) throw new IllegalArgumentException("Uso: create <nickname> <max_players>");
            int maxPlayers;
            try { maxPlayers = Integer.parseInt(args[args.length - 1]); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Errore: max_players deve essere un numero."); }
            if (maxPlayers < 2 || maxPlayers > 5) throw new IllegalArgumentException("Errore: max_players deve essere tra 2 e 5.");
            String nickname = String.join(" ", copyOfRange(args, 1, args.length - 1));
            this.pendingNickname = nickname;
            return new CreateGameCommand(controller, out, nickname, maxPlayers);
        });

        commandRegistry.put("join", args -> {
            if (args.length < 3) throw new IllegalArgumentException("Uso: join <nickname> <game_id>");
            String gameId = args[args.length - 1];
            String nickname = String.join(" ", copyOfRange(args, 1, args.length - 1));
            this.pendingNickname = nickname;
            return new JoinGameCommand(controller, out, nickname, gameId);
        });

        commandRegistry.put("list", args -> new AvailableGamesCommand(controller, out));
        commandRegistry.put("disconnect", args -> new DisconnectCommand(controller));
        commandRegistry.put("0", args -> new DisconnectCommand(controller));
    }

    @Override
    public void render() {
        String error = lobbyModel.consumeGlobalError();

        List<GameInfoDto> gamesToDisplay = null;
        lobbyModel.getReadLock().lock();
        try {
            if (showGamesList) {
                gamesToDisplay = lobbyModel.getAvailableGames();
            }
        } finally {
            lobbyModel.getReadLock().unlock();
        }

        renderer.render(gamesToDisplay, error);
    }

    @Override
    public void handleInput(String input) {
        if (input == null || input.isBlank()) return;

        String[] parts = input.trim().split("\\s+");
        String commandKey = parts[0].toLowerCase();

        CommandFactory factory = commandRegistry.get(commandKey);
        if (factory == null) {
            lobbyModel.setGlobalError("Comando sconosciuto. Usa: create, join, list, 0.");
            render();
            return;
        }

        try {
            GameCommand command = factory.create(parts);
            command.execute();
        } catch (IllegalArgumentException e) {
            lobbyModel.setGlobalError(e.getMessage());
            render();
        }
    }

    @Override
    public void onAvailableGames(List<GameInfoDto> games) {
        this.showGamesList = true;
        render();
    }

    @Override
    public void onError(String error) {
        render();
    }

    @Override
    public void onMatchmakingSuccess(String text) {
        //  MI DE-REGISTRO prima di morire
        notificationController.setMatchmakingView(null);

        //  va in Lobby
        session.setNickname(this.pendingNickname);
        navigator.toLobby();
    }

    @Override
    public void onServerDisconnected(String reason) {
        notificationController.setMatchmakingView(null);
        navigator.toLobby();
    }

}
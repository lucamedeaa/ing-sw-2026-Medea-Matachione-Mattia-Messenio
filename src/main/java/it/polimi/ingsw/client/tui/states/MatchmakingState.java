package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.commands.*;
import it.polimi.ingsw.client.tui.render.MatchmakingRenderer;
import it.polimi.ingsw.client.view.listeners.MatchmakingView;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.*;

import static java.util.Arrays.copyOfRange;

public class MatchmakingState implements UIState, MatchmakingView {
    private final NavigationPort nav;
    private final OutputPort out;
    private final MatchmakingRenderer renderer;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();
    private String pendingNickname = "";

    private boolean showGamesList = false; //mi serve per evitare stampe fasulle

    public MatchmakingState(NavigationPort nav, OutputPort out) {
        this.nav = nav;
        this.out = out;
        this.renderer = new MatchmakingRenderer(out);

        registerCommands();
        nav.getNotificationController().setMatchmakingView(this);

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
            return new CreateGameCommand(nav.getController(), out, nickname, maxPlayers);
        });

        commandRegistry.put("join", args -> {
            if (args.length < 3) throw new IllegalArgumentException("Uso: join <nickname> <game_id>");
            String gameId = args[args.length - 1];
            String nickname = String.join(" ", copyOfRange(args, 1, args.length - 1));
            this.pendingNickname = nickname;
            return new JoinGameCommand(nav.getController(), out, nickname, gameId);
        });

        commandRegistry.put("list", args -> new AvailableGamesCommand(nav.getController(), out));
        commandRegistry.put("disconnect", args -> new DisconnectCommand(nav.getController()));
        commandRegistry.put("0", args -> new DisconnectCommand(nav.getController()));
    }

    @Override
    public void render() {
        String error = nav.getLobbyModel().consumeGlobalError();

        List<GameInfoDTO> gamesToDisplay = null;
        nav.getLobbyModel().getReadLock().lock();
        try {
            if (showGamesList) {
                gamesToDisplay = nav.getLobbyModel().getAvailableGames();
            }
        } finally {
            nav.getLobbyModel().getReadLock().unlock();
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
            nav.getLobbyModel().setGlobalError("Comando sconosciuto. Usa: create, join, list, 0.");
            render();
            return;
        }

        try {
            GameCommand command = factory.create(parts);
            command.execute();
        } catch (IllegalArgumentException e) {
            nav.getLobbyModel().setGlobalError(e.getMessage());
            render();
        }
    }

    @Override
    public void onAvailableGames(List<GameInfoDTO> games) {
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
        nav.getNotificationController().setMatchmakingView(null);

        //  va in Lobby
        nav.setMyNickname(this.pendingNickname);
        nav.changeState(new LobbyState(nav, out));
    }

    @Override
    public void onServerDisconnected(String reason) {
        nav.getNotificationController().setMatchmakingView(null);
        nav.changeState(new DisconnectedState(out, reason));
    }

}
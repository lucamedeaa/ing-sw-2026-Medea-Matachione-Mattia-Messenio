package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.lightGameModel.LightPlayer;
import it.polimi.ingsw.client.tui.NavigationPort;
import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.client.tui.commands.*;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.client.tui.render.InGameRenderer;
import it.polimi.ingsw.client.view.listeners.InGameView;

import java.util.HashMap;
import java.util.Map;

public class InGameState implements UIState, InGameView {
    private final NavigationPort nav;
    private final OutputPort out;
    private final InGameRenderer renderer;
    private final Map<String, CommandFactory> commandRegistry = new HashMap<>();

    private Map<String, PlayerResources> prevState = new HashMap<>();
    private Map<String, PlayerResources> accumulated = new HashMap<>();
    private Map<String, PlayerResources> displayDeltas = new HashMap<>();
    private int lastRound;

    public InGameState(NavigationPort nav, OutputPort out) {
        this.nav = nav;
        this.out = out;
        this.renderer = new InGameRenderer(out);
        this.prevState = captureState();
        this.lastRound = nav.getMatchModel().getCurrentRound();
        registerCommands();

        nav.getNotificationController().setInGameView(this);
    }

    private void registerCommands() {
        commandRegistry.put("v", args -> new ViewTribeCommand(nav, out, args[1]));
        commandRegistry.put("i", args -> new InfoCommand(nav, out));
        commandRegistry.put("quit", args -> new DisconnectCommand(nav.getController()));
        commandRegistry.put("leave", args -> new LeaveGameCommand(nav.getController(), out));
    }

    @Override
    public void render() {
        String error = nav.getMatchModel().consumeGlobalError();
        if (error == null || error.isEmpty()) {
            error = nav.getLobbyModel().consumeGlobalError();
        }

        nav.getMatchModel().getReadLock().lock();
        try {
            updateDeltas();
        } finally {
            nav.getMatchModel().getReadLock().unlock();
        }

        renderer.render(nav.getMatchModel(), nav.getMyNickname(), displayDeltas, error);
    }

    @Override
    public void handleInput(String input) {
        if (nav.getMatchModel().isGameOver()) {
            nav.getNotificationController().setInGameView(null);
            nav.changeState(new GameEndedState(nav, out));
            return;
        }

        String[] parts = input.trim().split("\\s+");
        String key = parts[0].toLowerCase();

        if (key.matches("\\d+")) {
            new ActionCommand(nav, out, parts).execute();
        } else {
            CommandFactory factory = commandRegistry.get(key);
            if (factory != null) factory.create(parts).execute();
        }
    }

    private void updateDeltas() {
        Map<String, PlayerResources> currState = captureState();
        Map<String, PlayerResources> stepDeltas = computeDeltas(prevState, currState);

        if (isEndOfTurn()) {
            accumulated.clear();
        }

        mergeInto(accumulated, stepDeltas);
        displayDeltas = new HashMap<>(accumulated);
        prevState = currState;
    }

    private boolean isEndOfTurn() {
        int round = nav.getMatchModel().getCurrentRound();
        if (round != lastRound) {
            lastRound = round;
            return true;
        }
        return false;
    }

    @Override
    public void onDeltaEvent() {
    }
    @Override
    public void onError(String error) {

    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        nav.getNotificationController().setInGameView(null);
        //nav.getLobbyModel().setGlobalError(reason);
        nav.changeState(new MatchmakingState(nav, out));
    }

    private Map<String, PlayerResources> captureState() {
        Map<String, PlayerResources> snap = new HashMap<>();
        for (LightPlayer p : nav.getMatchModel().getPlayers().values()) {
            snap.put(p.getNickname(), new PlayerResources(p.getFood(), p.getPrestige(), p.getFoodDiscount()));
        }
        return snap;
    }

    private Map<String, PlayerResources> computeDeltas(Map<String, PlayerResources> prev, Map<String, PlayerResources> curr) {
        Map<String, PlayerResources> d = new HashMap<>();
        for (var e : curr.entrySet()) {
            PlayerResources p = prev.getOrDefault(e.getKey(), new PlayerResources(0, 0, 0));
            PlayerResources c = e.getValue();
            d.put(e.getKey(), new PlayerResources(
                    c.food() - p.food(),
                    c.prestige() - p.prestige(),
                    c.discount() - p.discount()
            ));
        }
        return d;
    }
    private void mergeInto(Map<String, PlayerResources> acc, Map<String, PlayerResources> step) {
        for (var e : step.entrySet()) {
            PlayerResources cur = acc.getOrDefault(e.getKey(), new PlayerResources(0, 0, 0));
            PlayerResources s = e.getValue();
            acc.put(e.getKey(), new PlayerResources(
                    cur.food() + s.food(),
                    cur.prestige() + s.prestige(),
                    cur.discount() + s.discount()
            ));
        }
    }

    @Override
    public void onServerDisconnected(String reason) {
        nav.getNotificationController().setInGameView(null);
        nav.changeState(new DisconnectedState(out, reason));
    }

    public record PlayerResources(int food, int prestige, int discount) {}
}
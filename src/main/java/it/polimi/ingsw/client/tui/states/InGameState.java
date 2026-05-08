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

    private Map<String, int[]> prevState = new HashMap<>();
    private Map<String, int[]> accumulated = new HashMap<>();
    private Map<String, int[]> displayDeltas = new HashMap<>();
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

        updateDeltas();

        String error = nav.getMatchModel().consumeGlobalError();
        if (error == null || error.isEmpty()) {
            error = nav.getLobbyModel().consumeGlobalError();
        }
        renderer.render(nav.getMatchModel(), nav.getMyNickname(), displayDeltas, error);

    }

    @Override
    public void handleInput(String input) {
        if (nav.getMatchModel().isGameOver()) {
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
        Map<String, int[]> currState = captureState();

        Map<String, int[]> stepDeltas = computeDeltas(prevState, currState);

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

    private Map<String, int[]> captureState() {
        Map<String, int[]> snap = new HashMap<>();
        for (LightPlayer p : nav.getMatchModel().getPlayers().values())
            snap.put(p.getNickname(), new int[]{p.getFood(), p.getPrestige(), p.getFoodDiscount()});
        return snap;
    }

    private Map<String, int[]> computeDeltas(Map<String, int[]> prev, Map<String, int[]> curr) {
        Map<String, int[]> d = new HashMap<>();
        for (var e : curr.entrySet()) {
            int[] p = prev.getOrDefault(e.getKey(), e.getValue());
            int discPrev = p.length > 2 ? p[2] : 0;
            int discCurr = e.getValue().length > 2 ? e.getValue()[2] : 0;
            d.put(e.getKey(), new int[]{e.getValue()[0] - p[0], e.getValue()[1] - p[1], discCurr - discPrev});
        }
        return d;
    }

    private void mergeInto(Map<String, int[]> acc, Map<String, int[]> step) {
        for (var e : step.entrySet()) {
            int[] cur = acc.getOrDefault(e.getKey(), new int[]{0, 0, 0});
            int stepDisc = e.getValue().length > 2 ? e.getValue()[2] : 0;
            int curDisc = cur.length > 2 ? cur[2] : 0;
            acc.put(e.getKey(), new int[]{cur[0] + e.getValue()[0], cur[1] + e.getValue()[1], curDisc + stepDisc});
        }
    }



}
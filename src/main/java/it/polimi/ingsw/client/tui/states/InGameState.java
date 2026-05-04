package it.polimi.ingsw.client.tui.states;

import it.polimi.ingsw.client.lightGameModel.LightPlayer;
import it.polimi.ingsw.client.tui.commands.*;
import it.polimi.ingsw.client.tui.render.ActionExecutor;
import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.client.tui.UIState;
import it.polimi.ingsw.network.dto.AvailableActionDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InGameState implements UIState {
    private final TUI tui;
    private Map<String, int[]> prevState     = new HashMap<>();
    private Map<String, int[]> accumulated   = new HashMap<>();
    private Map<String, int[]> displayDeltas = new HashMap<>();
    private int lastRound;
    private final Map<String, CommandFactory> commandRegistry;

    private String lastError = "";

    public InGameState(TUI tui) {
        this.tui = tui;
        prevState = captureState();
        lastRound = tui.getModel().getCurrentRound();
        this.commandRegistry = new HashMap<>();
        registerCommands();
    }

    @Override
    public void onModelUpdated() {
        Map<String, int[]> curr      = captureState();
        Map<String, int[]> stepDelta = computeDeltas(prevState, curr);
        prevState = curr;
        mergeInto(accumulated, stepDelta);
        if (isEndOfTurn()) {
            displayDeltas = new HashMap<>(accumulated);
            accumulated   = new HashMap<>();
        } else {
            displayDeltas = new HashMap<>();
        }

        render();
    }

    @Override
    public void render() {
            tui.renderInGame(tui.getModel().getMyActions(), displayDeltas, lastError);
            lastError = "";
        if (tui.getModel().isGameOver()) {
            tui.print("\033[1;33m  ══ PARTITA TERMINATA — Premi INVIO per vedere i risultati ══\033[0m");
        }
    }


    private void registerCommands() {
        // Manteniamo la factory SOLO per i comandi testuali locali
        commandRegistry.put("v", args -> {
            if (args.length < 2) throw new IllegalArgumentException("Uso: v <nickname>");
            return new ViewTribeCommand(tui, args[1]);
        });

        commandRegistry.put("i", args -> new InfoCommand(tui));
    }

    @Override
    public void handleInput(String input) {
        if (tui.getModel().isGameOver()) {
            tui.changeState(new GameEndedState(tui));
            return;
        }
        if (input == null || input.isBlank()) return;
        String[] parts = input.trim().split("\\s+");
        String key = parts[0].toLowerCase();

        GameCommand command;
        if (key.matches("\\d+")) {
            command = new ActionCommand(tui, parts);
        } else {
            CommandFactory factory = commandRegistry.get(key);
            if (factory == null) {
                onError("Comando sconosciuto.");
                return;
            }
            command = factory.create(parts);
        }
        command.execute();
    }

    @Override
    public void onError(String errorText) {
        this.lastError = errorText;
        render();
    }

    private boolean isEndOfTurn() {
        int round = tui.getModel().getCurrentRound();
        if (round != lastRound) {
            lastRound = round;
            return true;
        }
        return false;
    }

    private Map<String, int[]> captureState() {
        Map<String, int[]> snap = new HashMap<>();
        for (LightPlayer p : tui.getModel().getPlayers().values())
            snap.put(p.getNickname(), new int[]{p.getFood(), p.getPrestige()});
        return snap;
    }

    private Map<String, int[]> computeDeltas(Map<String, int[]> prev, Map<String, int[]> curr) {
        Map<String, int[]> d = new HashMap<>();
        for (var e : curr.entrySet()) {
            int[] p = prev.getOrDefault(e.getKey(), e.getValue());
            d.put(e.getKey(), new int[]{e.getValue()[0] - p[0], e.getValue()[1] - p[1]});
        }
        return d;
    }

    private void mergeInto(Map<String, int[]> acc, Map<String, int[]> step) {
        for (var e : step.entrySet()) {
            int[] cur = acc.getOrDefault(e.getKey(), new int[]{0, 0});
            acc.put(e.getKey(), new int[]{cur[0] + e.getValue()[0], cur[1] + e.getValue()[1]});
        }
    }


}
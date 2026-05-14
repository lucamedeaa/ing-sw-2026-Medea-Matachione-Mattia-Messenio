package it.polimi.ingsw.client.model.snapshot;

import it.polimi.ingsw.common.network.dto.action.ActionDto;
import java.util.ArrayList;
import java.util.List;

/**
 * Client-side snapshot of the active turn, available actions, and pending log lines.
 */
public class TurnSnapshot {
    private String activePlayer = "";
    private List<ActionDto> actions = new ArrayList<>();
    private final List<String> gameLogs = new ArrayList<>();

    public void setActivePlayerSnap(String activePlayer) { this.activePlayer = activePlayer; }
    public void setActions(List<ActionDto> actions) { this.actions = actions != null ? actions : new ArrayList<>(); }

    public void addGameLog(String log) { this.gameLogs.add(log); }
    public List<String> consumeGameLogs() {
        List<String> copy = new ArrayList<>(this.gameLogs);
        this.gameLogs.clear();
        return copy;
    }

    public String getActivePlayer() { return activePlayer; }
    public List<ActionDto> getActions() { return new ArrayList<>(actions); }
}
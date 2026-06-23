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

    /**
     * Stores the nickname of the active player.
     *
     * @param activePlayer active player nickname
     */
    public void setActivePlayerSnap(String activePlayer) { this.activePlayer = activePlayer; }
    /**
     * Replaces the currently available actions.
     *
     * @param actions available actions
     */
    public void setActions(List<ActionDto> actions) { this.actions = actions != null ? actions : new ArrayList<>(); }

    /**
     * Adds a game log line.
     *
     * @param log log message
     */
    public void addGameLog(String log) { this.gameLogs.add(log); }
    /**
     * Consumes the game logs.
     *
     * @return pending log lines, cleared from this snapshot
     */
    public List<String> consumeGameLogs() {
        List<String> copy = new ArrayList<>(this.gameLogs);
        this.gameLogs.clear();
        return copy;
    }

    /**
     * Returns the active player.
     *
     * @return the active player
     */
    public String getActivePlayer() { return activePlayer; }
    /**
     * Returns the actions.
     *
     * @return the actions
     */
    public List<ActionDto> getActions() { return new ArrayList<>(actions); }
}

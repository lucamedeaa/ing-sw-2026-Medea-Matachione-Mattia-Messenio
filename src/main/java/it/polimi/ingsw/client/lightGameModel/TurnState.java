package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.network.dto.AvailableActionDTO;
import java.util.ArrayList;
import java.util.List;

public class TurnState {
    private String activePlayer = "";
    private List<AvailableActionDTO> actions = new ArrayList<>();
    private final List<String> gameLogs = new ArrayList<>();

    public void setActivePlayer(String activePlayer) { this.activePlayer = activePlayer; }
    public void setActions(List<AvailableActionDTO> actions) { this.actions = actions != null ? actions : new ArrayList<>(); }

    public void addGameLog(String log) { this.gameLogs.add(log); }
    public List<String> consumeGameLogs() {
        List<String> copy = new ArrayList<>(this.gameLogs);
        this.gameLogs.clear();
        return copy;
    }

    public String getActivePlayer() { return activePlayer; }
    public List<AvailableActionDTO> getActions() { return new ArrayList<>(actions); }
}
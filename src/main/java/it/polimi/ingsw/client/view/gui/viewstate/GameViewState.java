package it.polimi.ingsw.client.view.gui.viewstate;

import java.util.List;
import java.util.Map;

public record GameViewState(
        BoardViewState           board,
        ActionsViewState         actions,
        List<PlayerInfo>         players,
        Map<String, List<Integer>> tribes,
        List<String>             newLogs,
        String                   selfNickname,
        String                   activePlayer
) {
    public GameViewState {
        players = players != null ? List.copyOf(players) : List.of();
        newLogs = newLogs != null ? List.copyOf(newLogs) : List.of();
        tribes  = tribes  != null ? Map.copyOf(tribes)   : Map.of();
    }
}
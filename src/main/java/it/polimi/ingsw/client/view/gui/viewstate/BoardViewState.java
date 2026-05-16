package it.polimi.ingsw.client.view.gui.viewstate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record BoardViewState(
        int playerCount,
        List<Integer> upperCards,
        List<Integer> lowerCards,
        List<PlayerInfo> players,
        Map<String, Integer> totemPositions,
        Map<String, Integer> returnPositions
) {
    public BoardViewState {
        // upperCards e lowerCards possono contenere null (slot vuoti della board)
        upperCards      = upperCards      != null ? Collections.unmodifiableList(new ArrayList<>(upperCards))  : List.of();
        lowerCards      = lowerCards      != null ? Collections.unmodifiableList(new ArrayList<>(lowerCards))  : List.of();
        players         = players         != null ? List.copyOf(players)        : List.of();
        totemPositions  = totemPositions  != null ? Map.copyOf(totemPositions)  : Map.of();
        returnPositions = returnPositions != null ? Map.copyOf(returnPositions) : Map.of();
    }
}
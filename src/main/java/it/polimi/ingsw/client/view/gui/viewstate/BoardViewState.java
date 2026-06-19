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
        Map<String, Integer> returnPositions,
        Integer nextDeckEra,
        Integer currentEra,
        Integer round
) {
    public BoardViewState {
        // upperCards/lowerCards may contain null (empty slots), so List.copyOf can't be used here
        upperCards      = upperCards      != null ? Collections.unmodifiableList(new ArrayList<>(upperCards))  : List.of();
        lowerCards      = lowerCards      != null ? Collections.unmodifiableList(new ArrayList<>(lowerCards))  : List.of();
        players         = players         != null ? List.copyOf(players)        : List.of();
        totemPositions  = totemPositions  != null ? Map.copyOf(totemPositions)  : Map.of();
        returnPositions = returnPositions != null ? Map.copyOf(returnPositions) : Map.of();
    }
}
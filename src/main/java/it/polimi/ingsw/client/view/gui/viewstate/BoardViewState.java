package it.polimi.ingsw.client.view.gui.viewstate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Immutable snapshot of the board area rendered by the GUI.
 *
 * @param playerCount number of players in the match
 * @param upperCards cards in the upper row, with null for empty slots
 * @param lowerCards cards in the lower row, with null for empty slots
 * @param players players visible on the board
 * @param totemPositions offer-track totem positions by nickname
 * @param returnPositions turn-order return positions by nickname
 * @param nextDeckEra era of the next main deck card
 * @param currentEra current game era
 * @param round current game round
 */
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
    /**
     * Copies incoming collections into immutable containers while preserving null card slots.
     */
    public BoardViewState {
        // upperCards/lowerCards may contain null (empty slots), so List.copyOf can't be used here
        upperCards      = upperCards      != null ? Collections.unmodifiableList(new ArrayList<>(upperCards))  : List.of();
        lowerCards      = lowerCards      != null ? Collections.unmodifiableList(new ArrayList<>(lowerCards))  : List.of();
        players         = players         != null ? List.copyOf(players)        : List.of();
        totemPositions  = totemPositions  != null ? Map.copyOf(totemPositions)  : Map.of();
        returnPositions = returnPositions != null ? Map.copyOf(returnPositions) : Map.of();
    }
}

package it.polimi.ingsw.client.view.gui.viewstate;

import it.polimi.ingsw.common.network.dto.action.ActionDto;

import java.util.List;
import java.util.Set;

/**
 * Immutable snapshot of action controls available to the local player.
 *
 * @param actions action DTOs available in the current state
 * @param isMyTurn true when the local player can execute actions
 * @param affordableCardIds selectable cards the local player can afford
 * @param unaffordableSelectableCardIds selectable cards that are visible but unaffordable
 */
public record ActionsViewState(
        List<ActionDto> actions,
        boolean isMyTurn,
        Set<Integer> affordableCardIds,
        Set<Integer> unaffordableSelectableCardIds
) {
    /**
     * Normalizes null collections to immutable empty collections.
     */
    public ActionsViewState {
        actions = actions != null ? List.copyOf(actions) : List.of();
        affordableCardIds = affordableCardIds != null ? Set.copyOf(affordableCardIds) : Set.of();
        unaffordableSelectableCardIds = unaffordableSelectableCardIds != null
                ? Set.copyOf(unaffordableSelectableCardIds) : Set.of();
    }
}

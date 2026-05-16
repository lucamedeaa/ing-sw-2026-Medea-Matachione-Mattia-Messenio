package it.polimi.ingsw.client.view.gui.viewstate;

import it.polimi.ingsw.common.network.dto.action.ActionDto;

import java.util.List;
import java.util.Set;

public record ActionsViewState(
        List<ActionDto> actions,
        boolean isMyTurn,
        Set<Integer> affordableCardIds,
        Set<Integer> unaffordableSelectableCardIds
) {
    public ActionsViewState {
        actions = actions != null ? List.copyOf(actions) : List.of();
        affordableCardIds = affordableCardIds != null ? Set.copyOf(affordableCardIds) : Set.of();
        unaffordableSelectableCardIds = unaffordableSelectableCardIds != null
                ? Set.copyOf(unaffordableSelectableCardIds) : Set.of();
    }
}
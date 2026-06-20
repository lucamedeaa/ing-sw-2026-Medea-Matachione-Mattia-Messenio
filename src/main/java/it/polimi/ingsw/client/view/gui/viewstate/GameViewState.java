package it.polimi.ingsw.client.view.gui.viewstate;

import java.util.List;
import java.util.Map;

/**
 * Immutable aggregate snapshot for the main in-game screen.
 *
 * @param board board state to render
 * @param actions action state to render
 * @param players player summary rows
 * @param tribes tribe cards by player nickname
 * @param newLogs newly consumed log messages
 * @param selfNickname nickname of the local player
 * @param activePlayer nickname of the active player
 */
public record GameViewState(
        BoardViewState           board,
        ActionsViewState         actions,
        List<PlayerInfo>         players,
        Map<String, List<Integer>> tribes,
        List<String>             newLogs,
        String                   selfNickname,
        String                   activePlayer
) {
    /**
     * Copies incoming collections into immutable containers.
     */
    public GameViewState {
        players = players != null ? List.copyOf(players) : List.of();
        newLogs = newLogs != null ? List.copyOf(newLogs) : List.of();
        tribes  = tribes  != null ? Map.copyOf(tribes)   : Map.of();
    }
}

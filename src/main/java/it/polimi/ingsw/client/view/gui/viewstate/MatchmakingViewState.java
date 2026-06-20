package it.polimi.ingsw.client.view.gui.viewstate;

import it.polimi.ingsw.common.network.dto.GameInfoDto;
import java.util.List;

/**
 * Immutable snapshot rendered by the matchmaking screen.
 *
 * @param games available games to show
 * @param error current error or information message
 */
public record MatchmakingViewState(List<GameInfoDto> games, String error) {
    /**
     * Normalizes null values and copies the games list.
     */
    public MatchmakingViewState {
        games = (games != null) ? List.copyOf(games) : List.of();
        error = (error != null) ? error : "";
    }
}

package it.polimi.ingsw.client.view.gui.viewstate;

import it.polimi.ingsw.common.network.dto.GameInfoDto;
import java.util.List;

public record MatchmakingViewState(List<GameInfoDto> games, String error) {
    public MatchmakingViewState {
        games = (games != null) ? List.copyOf(games) : null;
        error = (error != null) ? error : "";
    }
}
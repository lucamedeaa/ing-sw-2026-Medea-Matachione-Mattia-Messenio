package it.polimi.ingsw.client.view.listeners;

import it.polimi.ingsw.common.network.dto.GameInfoDto;

import java.util.List;

/** Defines the contract for matchmaking view. */
public interface MatchmakingView {
    void onAvailableGames(List<GameInfoDto> games);
    void onMatchmakingSuccess(String text);
    void onError(String error);
    void onServerDisconnected(String reason);
}

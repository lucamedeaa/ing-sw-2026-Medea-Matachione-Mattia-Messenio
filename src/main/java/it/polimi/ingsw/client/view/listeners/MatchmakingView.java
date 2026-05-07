package it.polimi.ingsw.client.view.listeners;

import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;

public interface MatchmakingView {
    void onAvailableGames(List<GameInfoDTO> games);
    void onMatchmakingSuccess(String text);
    void onError(String error);
}

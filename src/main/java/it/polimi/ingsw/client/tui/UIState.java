package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.network.messages.GameInfoDTO;
import java.util.List;

public interface UIState {
    void render();
    void handleInput(String input);
    default void onMatchmakingSuccess(String text)         {}
    default void onAvailableGames(List<GameInfoDTO> games) {}
    default void onError(String errorText)                 {}
    default void onModelUpdated() {}
    default void onGameAborted(String reason) {}
    default void onRoomUpdate(List<String> strings, String notification) {}
    default void onGameStarted() {}
    default void onGameLeft() {}
}

package it.polimi.ingsw.client.view.listeners;

import java.util.List;

public interface LobbyView {
    void onRoomUpdate(String notification, List<String> currentPlayers);
    void onGameStarted();
    void onReturnToMatchmaking(String reason);
    void onError(String error);
}

package it.polimi.ingsw.client.view.gui.viewstate;

import java.util.List;

public record LobbyViewState(List<String> players, String selfNickname, String notification) {
    public LobbyViewState {
        players      = (players != null)      ? List.copyOf(players) : List.of();
        selfNickname = (selfNickname != null) ? selfNickname : "";
        notification = (notification != null) ? notification : "";
    }
}
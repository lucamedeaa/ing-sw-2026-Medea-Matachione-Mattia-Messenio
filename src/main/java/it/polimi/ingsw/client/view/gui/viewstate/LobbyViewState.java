package it.polimi.ingsw.client.view.gui.viewstate;

import java.util.List;

/**
 * Immutable snapshot rendered by the lobby screen.
 *
 * @param players nicknames currently in the lobby
 * @param selfNickname nickname of the local player
 * @param notification latest lobby notification
 */
public record LobbyViewState(List<String> players, String selfNickname, String notification) {
    /**
     * Normalizes null values and copies the players list.
     */
    public LobbyViewState {
        players      = (players != null)      ? List.copyOf(players) : List.of();
        selfNickname = (selfNickname != null) ? selfNickname : "";
        notification = (notification != null) ? notification : "";
    }
}

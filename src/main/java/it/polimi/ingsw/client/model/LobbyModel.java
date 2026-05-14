package it.polimi.ingsw.client.model;

import it.polimi.ingsw.common.network.dto.GameInfoDto;
import java.util.ArrayList;
import java.util.List;

//GESTISCE PRE PARTITA

/**
 * Client-side state for matchmaking and lobby screens.
 */
public class LobbyModel extends ObservableModel {
    private List<GameInfoDto> availableGames = new ArrayList<>();
    private List<String> lobbyPlayers = new ArrayList<>();
    private String lobbyNotification = "";

    /**
     * Replaces the list of games available for joining.
     *
     * @param games latest lobby game list received from the server
     */
    public void setAvailableGames(List<GameInfoDto> games) {

            this.availableGames = games;

    }

    /**
     * Updates the current room roster and last lobby notification.
     *
     * @param players nicknames currently in the room
     * @param notification message associated with the update
     */
    public void setLobbyData(List<String> players, String notification) {

            this.lobbyPlayers = new ArrayList<>(players);
            this.lobbyNotification = notification;


        notifyUI();

    }

    public List<GameInfoDto> getAvailableGames() {

            return new ArrayList<>(availableGames);

    }

    public List<String> getLobbyPlayers() {

            return new ArrayList<>(lobbyPlayers);

    }

    public String getLobbyNotification() {

            return lobbyNotification;

    }
}

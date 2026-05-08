package it.polimi.ingsw.client.lightGameModel;

import it.polimi.ingsw.network.messages.GameInfoDTO;
import java.util.ArrayList;
import java.util.List;

//GESTISCE PRE PARTITA

public class LobbyModel extends ObservableModel {
    private List<GameInfoDTO> availableGames = new ArrayList<>();
    private List<String> lobbyPlayers = new ArrayList<>();
    private String lobbyNotification = "";
    private String globalError = "";

    public void setAvailableGames(List<GameInfoDTO> games) {
        this.availableGames = games;
    }

    public void setLobbyData(List<String> players, String notification) {
        this.lobbyPlayers = new ArrayList<>(players);
        this.lobbyNotification = notification;
        notifyUI();
    }



    public List<GameInfoDTO> getAvailableGames() { return new ArrayList<>(availableGames); }
    public List<String> getLobbyPlayers() { return new ArrayList<>(lobbyPlayers); }
    public String getLobbyNotification() { return lobbyNotification; }
}
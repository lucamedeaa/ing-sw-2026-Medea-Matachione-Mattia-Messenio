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
        lock.writeLock().lock();
        try {
            this.availableGames = games;
        }finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Updates the current room roster and last lobby notification.
     *
     * @param players nicknames currently in the room
     * @param notification message associated with the update
     */
    public void setLobbyData(List<String> players, String notification) {
        lock.writeLock().lock();
        try {
            this.lobbyPlayers = new ArrayList<>(players);
            this.lobbyNotification = notification;

        }finally {
            lock.writeLock().unlock();
        }
        notifyUI();

    }

    public List<GameInfoDto> getAvailableGames() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(availableGames);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<String> getLobbyPlayers() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(lobbyPlayers);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getLobbyNotification() {
        lock.readLock().lock();
        try {
            return lobbyNotification;
        } finally {
            lock.readLock().unlock();
        }
    }
}

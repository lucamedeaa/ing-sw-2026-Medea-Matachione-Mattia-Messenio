package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;

public class GameInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String gameId;
    private final String creatorNickname;
    private final int currentPlayers;
    private final int maxPlayers;

    public GameInfoDto(String gameId, String creatorNickname, int currentPlayers, int maxPlayers) {
        this.gameId = gameId;
        this.creatorNickname = creatorNickname;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
    }

    public String getGameId() {
        return gameId;
    }

    public String getCreatorNickname() {
        return creatorNickname;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public String toString() {
        return "Partita [" + gameId + "] - Creatore: " + creatorNickname + " (" + currentPlayers + "/" + maxPlayers + ")";
    }
}

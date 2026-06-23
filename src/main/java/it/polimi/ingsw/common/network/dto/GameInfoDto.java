package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Serializable summary of a lobby game shown to clients before joining.
 */
public class GameInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String gameId;
    private final String creatorNickname;
    private final int currentPlayers;
    private final int maxPlayers;

    /**
     * Creates a lobby game summary.
     *
     * @param gameId unique game identifier
     * @param creatorNickname nickname of the player who created the game
     * @param currentPlayers number of players already in the room
     * @param maxPlayers maximum number of players accepted by the room
     */
    public GameInfoDto(String gameId, String creatorNickname, int currentPlayers, int maxPlayers) {
        this.gameId = gameId;
        this.creatorNickname = creatorNickname;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
    }

    /**
     * Returns the game id.
     *
     * @return the game identifier
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Returns the creator nickname.
     *
     * @return the creator nickname
     */
    public String getCreatorNickname() {
        return creatorNickname;
    }

    /**
     * Returns the current players.
     *
     * @return the current players
     */
    public int getCurrentPlayers() {
        return currentPlayers;
    }

    /**
     * Returns the max players.
     *
     * @return the maximum number of players
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Game [" + gameId + "] - Founder: " + creatorNickname + " (" + currentPlayers + "/" + maxPlayers + ")";
    }
}

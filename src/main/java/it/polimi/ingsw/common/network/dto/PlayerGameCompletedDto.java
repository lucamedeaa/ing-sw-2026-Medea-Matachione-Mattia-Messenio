package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * End-game result for the local player, including personal leaderboard data.
 *
 * @param playerCount number of players in the completed game
 * @param localPosition final position of the local player
 * @param localScore final score of the local player
 * @param localRemainingFood remaining food of the local player
 * @param globalPersonalBestPosition best leaderboard position for the local player
 * @param personalBestScore best stored score for the local player
 * @param personalBestRemainingFood remaining food associated with the personal best
 */
public record PlayerGameCompletedDto(
        int playerCount,
        int localPosition,
        int localScore,
        int localRemainingFood,
        int globalPersonalBestPosition,
        int personalBestScore,
        int personalBestRemainingFood
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

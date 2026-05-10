package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Final score row used by game-over notifications.
 *
 * @param nickname player nickname
 * @param finalScore final score reached by the player
 * @param remainingFood food left at game end
 */
public record PlayerScoreDto(String nickname, int finalScore, int remainingFood) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

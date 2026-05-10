package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.PlayerScoreDto;

/**
 * Server-side final score update for one player.
 *
 * @param nickname player nickname
 * @param finalScore final score
 * @param remainingFood food left at game end
 */
public record PlayerScoreUpdate(String nickname, int finalScore, int remainingFood) {
    /**
     * Converts this update to its network DTO.
     *
     * @return player score DTO
     */
    public PlayerScoreDto toDTO() {
        return new PlayerScoreDto(nickname, finalScore, remainingFood);
    }
}
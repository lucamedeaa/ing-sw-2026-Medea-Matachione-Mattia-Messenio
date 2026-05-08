package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.common.network.dto.PlayerScoreDto;

public record PlayerScoreUpdate(String nickname, int finalScore, int remainingFood) {
    public PlayerScoreDto toDTO() {
        return new PlayerScoreDto(nickname, finalScore, remainingFood);
    }
}
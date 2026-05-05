package it.polimi.ingsw.model.updates;

import it.polimi.ingsw.network.dto.PlayerScoreDTO;

public record PlayerScoreUpdate(String nickname, int finalScore, int remainingFood) {
    public PlayerScoreDTO toDTO() {
        return new PlayerScoreDTO(nickname, finalScore, remainingFood);
    }
}
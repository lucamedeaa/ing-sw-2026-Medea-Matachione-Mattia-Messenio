package it.polimi.ingsw.common.network.dto;

import java.io.Serial;
import java.io.Serializable;

public record PlayerScoreDto(String nickname, int finalScore, int remainingFood) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

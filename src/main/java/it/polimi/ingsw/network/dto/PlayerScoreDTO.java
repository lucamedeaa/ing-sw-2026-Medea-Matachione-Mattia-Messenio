package it.polimi.ingsw.network.dto;

import java.io.Serial;
import java.io.Serializable;

public record PlayerScoreDTO(String nickname, int finalScore, int remainingFood) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

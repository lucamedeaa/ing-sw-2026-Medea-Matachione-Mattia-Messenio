package it.polimi.ingsw.network.dto;

import it.polimi.ingsw.model.enums.TotemColor;
import java.io.Serial;
import java.io.Serializable;

/** Data transfer object representing a player's public state (nickname, food, and prestige). */
public record PlayerDTO(String nickname, int food, int prestige, TotemColor totemColor,int foodDiscount) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

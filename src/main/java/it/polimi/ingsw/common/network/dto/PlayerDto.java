package it.polimi.ingsw.common.network.dto;

import it.polimi.ingsw.server.model.enums.TotemColor;
import java.io.Serial;
import java.io.Serializable;

/** Data transfer object representing a player's public state (nickname, food, and prestige). */
public record PlayerDto(String nickname, int food, int prestige, TotemColor totemColor, int foodDiscount, int sustenanceDiscount) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

package it.polimi.ingsw.common.network.dto;

import it.polimi.ingsw.server.model.enums.TotemColor;
import java.io.Serial;
import java.io.Serializable;

/**
 * Data transfer object representing a player's public state.
 *
 * @param nickname player nickname
 * @param food current food amount
 * @param prestige current prestige points
 * @param totemColor color assigned to the player's totem
 * @param foodDiscount permanent food discount from cards
 * @param sustenanceDiscount discount applied during Sustenance
 */
public record PlayerDto(String nickname, int food, int prestige, TotemColor totemColor, int foodDiscount, int sustenanceDiscount) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

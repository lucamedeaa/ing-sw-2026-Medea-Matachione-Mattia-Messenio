package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.server.model.enums.TotemColor;
import it.polimi.ingsw.common.network.dto.PlayerDto;

/**
 * Server-side public player-state update.
 *
 * @param nickname player nickname
 * @param food current food
 * @param prestige current prestige
 * @param totemColor assigned totem color
 * @param foodDiscount permanent food discount
 * @param sustenanceDiscount Sustenance-specific discount
 */
public record PlayerUpdate(String nickname, int food, int prestige, TotemColor totemColor, int foodDiscount, int sustenanceDiscount) {
    /**
     * Converts this update to its network DTO.
     *
     * @return player DTO
     */
    public PlayerDto toDTO() { return new PlayerDto(nickname, food, prestige, totemColor, foodDiscount, sustenanceDiscount); }
}
package it.polimi.ingsw.server.model.update;

import it.polimi.ingsw.server.model.enums.TotemColor;
import it.polimi.ingsw.common.network.dto.PlayerDto;

public record PlayerUpdate(String nickname, int food, int prestige, TotemColor totemColor, int foodDiscount, int sustenanceDiscount) {
    public PlayerDto toDTO() { return new PlayerDto(nickname, food, prestige, totemColor, foodDiscount, sustenanceDiscount); }
}
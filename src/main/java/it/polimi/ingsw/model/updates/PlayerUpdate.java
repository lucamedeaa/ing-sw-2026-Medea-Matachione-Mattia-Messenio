package it.polimi.ingsw.model.updates;

import it.polimi.ingsw.model.enums.TotemColor;
import it.polimi.ingsw.network.dto.PlayerDTO;

public record PlayerUpdate(String nickname, int food, int prestige, TotemColor totemColor, int foodDiscount) {
    public PlayerDTO toDTO() {
        return new PlayerDTO(nickname, food, prestige, totemColor, foodDiscount);
    }
}
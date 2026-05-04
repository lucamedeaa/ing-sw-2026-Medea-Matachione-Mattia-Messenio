package it.polimi.ingsw.model.updates;
import it.polimi.ingsw.model.enums.TotemColor;

public record PlayerUpdate(String nickname, int food, int prestige, TotemColor totemColor, int foodDiscount) {
}

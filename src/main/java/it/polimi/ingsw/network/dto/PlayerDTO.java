package it.polimi.ingsw.network.dto;

import java.io.Serializable;

public record PlayerDTO(String nickname, int food, int prestige) implements Serializable {

}

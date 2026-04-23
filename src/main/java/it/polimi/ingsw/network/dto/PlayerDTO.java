package it.polimi.ingsw.network.dto;

import java.io.Serializable;

/** Data transfer object representing a player's public state (nickname, food, and prestige). */
public record PlayerDTO(String nickname, int food, int prestige) implements Serializable {
}
package it.polimi.ingsw.network.dto;

import java.io.Serializable;

public record PlayerScoreDTO(String nickname, int finalScore, int remainingFood) implements Serializable {}
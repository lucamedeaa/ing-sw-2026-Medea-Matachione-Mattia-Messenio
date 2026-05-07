package it.polimi.ingsw.model;

import java.util.List;

public record CompletedGameResult(List<PlayerGameResult> playerResults) {
    public CompletedGameResult {
        playerResults = List.copyOf(playerResults);
    }
}

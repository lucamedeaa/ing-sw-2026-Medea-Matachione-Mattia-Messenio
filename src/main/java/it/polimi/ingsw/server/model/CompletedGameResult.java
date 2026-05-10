package it.polimi.ingsw.server.model;

import java.util.List;

/**
 * Immutable result of a normally completed game.
 *
 * @param playerResults final results ordered by ranking position
 */
public record CompletedGameResult(List<PlayerGameResult> playerResults) {
    public CompletedGameResult {
        playerResults = List.copyOf(playerResults);
    }
}

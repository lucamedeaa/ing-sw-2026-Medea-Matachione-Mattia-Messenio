package it.polimi.ingsw.server.model;

/**
 * Final result for one player in a completed game.
 *
 * @param position ranking position, starting from 1
 * @param nickname player nickname
 * @param finalScore final score reached by the player
 * @param remainingFood food left at game end
 */
public record PlayerGameResult(int position, String nickname, int finalScore, int remainingFood) {
}

package it.polimi.ingsw.server.model;

/**
 * Callback notified when a game completes normally and can be persisted or closed.
 */
@FunctionalInterface
public interface GameCompletionHandler {
    /**
     * Handles a completed-game result.
     *
     * @param result final completed-game result
     */
    void onGameCompleted(CompletedGameResult result);
}

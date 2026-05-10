package it.polimi.ingsw.server.model.exception;

/**
 * Raised when leaderboard persistence or retrieval fails.
 */
public class LeaderboardStorageException extends RuntimeException {
    /**
     * Creates a storage exception with the original cause.
     *
     * @param message failure message
     * @param cause original failure
     */
    public LeaderboardStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

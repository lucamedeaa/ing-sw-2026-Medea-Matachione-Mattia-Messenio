package it.polimi.ingsw.server.model.exception;

/**
 * Raised when a lobby or matchmaking action is invalid.
 */
public class LobbyActionException extends Exception {
    /**
     * Creates a lobby action exception.
     *
     * @param message failure message
     */
    public LobbyActionException(String message) {
        super(message);
    }
}

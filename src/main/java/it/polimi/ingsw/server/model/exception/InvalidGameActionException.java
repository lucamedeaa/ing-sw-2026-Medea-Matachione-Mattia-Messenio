package it.polimi.ingsw.server.model.exception;

/** Raised when a player requests an action that is not legal in the current game state. */
public class InvalidGameActionException extends RuntimeException {

    /**
     * Creates a new {@code InvalidGameActionException} instance.
     *
     * @param message message to process
     */
    public InvalidGameActionException(String message) {
        super(message);
    }

    /**
     * Creates a new {@code InvalidGameActionException} instance.
     *
     * @param message message to process
     * @param cause cause
     */
    public InvalidGameActionException(String message, Throwable cause) {
        super(message, cause);
    }
}

package it.polimi.ingsw.server.model.exception;

/** Raised when a player requests an action that is not legal in the current game state. */
public class InvalidGameActionException extends RuntimeException {

    public InvalidGameActionException(String message) {
        super(message);
    }

    public InvalidGameActionException(String message, Throwable cause) {
        super(message, cause);
    }
}

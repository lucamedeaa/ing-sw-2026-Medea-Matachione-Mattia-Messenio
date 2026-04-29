package it.polimi.ingsw.server.exceptions;

public class InvalidPlayerCountException extends Exception {
    public InvalidPlayerCountException(String message) {
        super(message);
    }
}
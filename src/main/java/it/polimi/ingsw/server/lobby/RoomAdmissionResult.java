package it.polimi.ingsw.server.lobby;

/**
 * Immutable value object for room admission result.
 *
 * @param consequence action to run after matchmaking succeeds
 */
public record RoomAdmissionResult(Runnable consequence) {

    /**
     * Runs the consequence associated with a successful matchmaking operation.
     */
    public void afterMatchmakingSuccess() {
        consequence.run();
    }
}

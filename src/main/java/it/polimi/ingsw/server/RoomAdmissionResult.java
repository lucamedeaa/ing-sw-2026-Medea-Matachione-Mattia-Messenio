package it.polimi.ingsw.server;

public record RoomAdmissionResult(Runnable consequence) {

    public void afterMatchmakingSuccess() {
        consequence.run();
    }
}

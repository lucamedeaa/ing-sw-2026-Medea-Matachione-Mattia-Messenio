package it.polimi.ingsw.server.lobby;

public record RoomAdmissionResult(Runnable consequence) {

    public void afterMatchmakingSuccess() {
        consequence.run();
    }
}

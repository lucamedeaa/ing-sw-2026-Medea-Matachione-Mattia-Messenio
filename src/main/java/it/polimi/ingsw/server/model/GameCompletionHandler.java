package it.polimi.ingsw.server.model;

@FunctionalInterface
public interface GameCompletionHandler {
    void onGameCompleted(CompletedGameResult result);
}

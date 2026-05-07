package it.polimi.ingsw.model;

@FunctionalInterface
public interface GameCompletionHandler {
    void onGameCompleted(CompletedGameResult result);
}

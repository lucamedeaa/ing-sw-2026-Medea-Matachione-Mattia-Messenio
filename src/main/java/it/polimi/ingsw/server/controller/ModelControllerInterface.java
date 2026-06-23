package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.GameCompletionHandler;

/** Defines the contract for model controller interface. */
public interface ModelControllerInterface {
    void takeCard(String nickname, int row, int col);
    void placeTotem(String nickname, int positionIndex);
    void skipBonus(String nickname);
    void commitEvents();
    void setCompletionHandler(GameCompletionHandler completionHandler);
    boolean abort();
}

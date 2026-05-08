package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.GameCompletionHandler;

public interface ModelControllerInterface {
    void takeCard(String nickname, int row, int col);
    void placeTotem(String nickname, int positionIndex);
    void skipBonus(String nickname);
    void commitEvents();
    void setCompletionHandler(GameCompletionHandler completionHandler);
    boolean abort();
}

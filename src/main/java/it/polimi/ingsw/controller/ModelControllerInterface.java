package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.updates.GameEvent;

public interface ModelControllerInterface {
    void takeCard(Player nickname, int row, int col);
    void placeTotem(Player nickname, int positionIndex);
    void skipBonus(Player nickname);
    Player getPlayerByNickname(String nickname);
    void commitEvents();
    void pushEvent(GameEvent event);
}

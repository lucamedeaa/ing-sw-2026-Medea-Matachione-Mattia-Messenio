package it.polimi.ingsw.client.view.gui.controllers;

import java.util.List;
import java.util.Set;

public interface ActionCommandHost {
    void promptCardSelection(int upper, int lower, Set<Integer> affordableIds, Set<Integer> unaffordableIds);
    void promptTotemPlacement(List<Integer> tiles);
    void skipAction();
    void leaveGame();
    void toggleLog();
    void disconnect();
    void showInfo();
}
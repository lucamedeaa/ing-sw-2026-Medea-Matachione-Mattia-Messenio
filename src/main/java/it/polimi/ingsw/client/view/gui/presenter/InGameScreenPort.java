package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.viewstate.GameViewState;

public interface InGameScreenPort {
    void doRefresh(GameViewState state);
    void showError(String error);
}
package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.viewstate.GameEndedViewState;

public interface GameEndedScreenPort {
    void render(GameEndedViewState state);
    void showSpinner();
}
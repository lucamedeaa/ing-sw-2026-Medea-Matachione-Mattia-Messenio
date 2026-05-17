package it.polimi.ingsw.client.view.gui.presenter;

import it.polimi.ingsw.client.view.gui.viewstate.LobbyViewState;

public interface LobbyScreenPort {
    void render(LobbyViewState state);
    void showError(String error);
}
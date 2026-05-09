package it.polimi.ingsw.client.view.tui;

import it.polimi.ingsw.client.view.tui.state.UIState;

// permette al router di cambiare lo stato attivo nella TUi
public interface StateContainer {
    void updateState(UIState newState);
}
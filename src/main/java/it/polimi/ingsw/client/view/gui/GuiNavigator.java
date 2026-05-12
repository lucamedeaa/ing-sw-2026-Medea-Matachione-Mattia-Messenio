package it.polimi.ingsw.client.view.gui;

public interface GuiNavigator {
    void toMatchmaking();
    void toLobby();
    void toInGame();
    void toGameEnded();
    void toDisconnected(String reason);
}


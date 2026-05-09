package it.polimi.ingsw.client.view.tui;

public interface TuiNavigator {
    void toMatchmaking();
    void toLobby();
    void toInGame();
    void toViewTribe(String targetPlayer);
    void toInfo();
    void toGameEnded();
    void toDisconnected(String reason);
}
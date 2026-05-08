package it.polimi.ingsw.client.view.tui;

import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.tui.command.ServerCommandPort;
import it.polimi.ingsw.client.view.tui.state.UIState;

public interface NavigationPort {
    void changeState(UIState newState);
    LobbyModel getLobbyModel();
    GameModel getMatchModel();
    ServerCommandPort getController();
    void setMyNickname(String nickname);
    String getMyNickname();

    ClientNotificationController getNotificationController();
}
package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.client.lightGameModel.LightGameModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.network.ServerController;

public interface NavigationPort {
    void changeState(UIState newState);
    LightGameModel getModel();
    ServerController getController();
    void setMyNickname(String nickname);
    String getMyNickname();

    ClientNotificationController getNotificationController();
}
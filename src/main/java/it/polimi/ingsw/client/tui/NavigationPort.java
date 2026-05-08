package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.client.lightGameModel.LobbyModel;
import it.polimi.ingsw.client.lightGameModel.MatchModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.network.ServerController;
import it.polimi.ingsw.controller.LobbyController;

import javax.swing.*;

public interface NavigationPort {
    void changeState(UIState newState);
    LobbyModel getLobbyModel();
    MatchModel getMatchModel();
    ServerCommandPort getController();
    void setMyNickname(String nickname);
    String getMyNickname();

    ClientNotificationController getNotificationController();
}
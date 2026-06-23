package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.controller.ServerController;

/** Defines the contract for client ui. */
public interface ClientUi {
        void setController(ServerController controller);
        void start();
        void setNotificationController(ClientNotificationController notificationController);
}
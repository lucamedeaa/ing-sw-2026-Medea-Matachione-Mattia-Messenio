package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;

public record GuiContext(
        ServerController controller,
        LobbyModel lobbyModel,
        GameModel gameModel,
        ClientSession session,
        ClientNotificationController notificationController
) {}
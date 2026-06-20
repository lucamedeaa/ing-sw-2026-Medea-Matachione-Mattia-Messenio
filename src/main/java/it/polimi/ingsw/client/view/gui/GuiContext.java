package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;

/**
 * Shared dependencies passed to GUI presenters and controllers.
 *
 * @param controller server command controller
 * @param lobbyModel client-side lobby model
 * @param gameModel client-side game model
 * @param session local client session
 * @param notificationController dispatcher for server notifications
 * @param lifecycle lifecycle port used to close the GUI
 * @param scheduler JavaFX thread scheduler
 */
public record GuiContext(
        ServerController controller,
        LobbyModel lobbyModel,
        GameModel gameModel,
        ClientSession session,
        ClientNotificationController notificationController,
        GuiLifecyclePort lifecycle,
        FxScheduler scheduler
) {}

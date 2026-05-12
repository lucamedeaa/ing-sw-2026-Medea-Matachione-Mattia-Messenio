package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.ClientUi;
import javafx.application.Application;
import javafx.stage.Stage;

public class GuiFxApp extends Application implements ClientUi{
    private static ServerController staticController;
    private static GameModel gameModel;
    private static ClientSession session;
    private static LobbyModel lobbyModel;
    private static ClientNotificationController notificationController;

    public GuiFxApp() {
        // Required by JavaFX
    }

    public GuiFxApp(LobbyModel lobbyModel, GameModel gameModel) {
        GuiFxApp.gameModel = gameModel;
        session = new ClientSession();
        GuiFxApp.lobbyModel = lobbyModel;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GuiContext ctx = new GuiContext(staticController, lobbyModel, gameModel, session, notificationController);
        GuiFxRouter router = new GuiFxRouter(primaryStage, ctx);
        primaryStage.setTitle("Mesos");
        router.toMatchmaking();
        primaryStage.show();
    }

    @Override
    public void setController(ServerController controller) {
        staticController = controller;
    }

    @Override
    public void start() {
        Application.launch(GuiFxApp.class);
    }

    @Override
    public void setNotificationController(ClientNotificationController notificationController) {
        GuiFxApp.notificationController = notificationController;
    }
}

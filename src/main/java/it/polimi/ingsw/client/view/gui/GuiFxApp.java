package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.ClientUi;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.text.Font;

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
        Font.loadFont(getClass().getResourceAsStream("/fonts/MedievalSharp-Regular.ttf"), 14);
        GuiContext ctx = new GuiContext(staticController, lobbyModel, gameModel, session, notificationController);
        GuiFxRouter router = new GuiFxRouter(primaryStage, ctx);
        primaryStage.setTitle("Mesos");

        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);

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
    //Entry point della GUI. Estende Application, implementa ClientUi.
    // Crea ClientSession internamente. Salva tutte le dipendenze in campi statici perché JavaFX instanzia una seconda copia via reflection;
    // start(Stage) le legge e costruisce il router. Chiamare start() lancia la finestra e blocca il thread fino alla chiusura.

}

package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.controller.ServerController;
import it.polimi.ingsw.client.model.ClientSession;
import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.LobbyModel;
import it.polimi.ingsw.client.network.ClientNotificationController;
import it.polimi.ingsw.client.view.ClientUi;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.text.Font;

/**
 * JavaFX implementation of the client UI entry point.
 */
public class GuiFxApp extends Application implements ClientUi, GuiLifecyclePort{
    private static ServerController staticController;
    private static GameModel gameModel;
    private static ClientSession session;
    private static LobbyModel lobbyModel;
    private static ClientNotificationController notificationController;

    /**
     * Required no-argument constructor used by the JavaFX launcher.
     */
    public GuiFxApp() {
        // Required by JavaFX
    }

    /**
     * Creates a GUI application bound to the shared client models.
     *
     * @param lobbyModel client-side lobby model
     * @param gameModel client-side game model
     */
    public GuiFxApp(LobbyModel lobbyModel, GameModel gameModel) {
        GuiFxApp.gameModel = gameModel;
        session = new ClientSession();
        GuiFxApp.lobbyModel = lobbyModel;
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream(GuiAssetPaths.FONT_MEDIEVAL), 14);
        GuiContext ctx = new GuiContext(staticController, lobbyModel, gameModel, session,
                notificationController, this, Platform::runLater);
        GuiFxRouter router = new GuiFxRouter(primaryStage, ctx);
        primaryStage.setTitle("Mesos");

        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);

        router.toMatchmaking();
        primaryStage.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setController(ServerController controller) {
        staticController = controller;
    }

    /** {@inheritDoc} */
    @Override
    public void start() {
        Application.launch(GuiFxApp.class);
    }

    /** {@inheritDoc} */
    @Override
    public void setNotificationController(ClientNotificationController notificationController) {
        GuiFxApp.notificationController = notificationController;
    }

    /** {@inheritDoc} */
    @Override
    public void requestShutdown() {
        Platform.exit();
        System.exit(0);
    }
}

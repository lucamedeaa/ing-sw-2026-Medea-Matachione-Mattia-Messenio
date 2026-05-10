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
    private GameModel gameModel;
    private ClientSession session;
    private LobbyModel lobbyModel;

    public GuiFxApp() {
        // Required by JavaFX
    }

    public GuiFxApp(LobbyModel lobbyModel, GameModel gameModel) {
        this.gameModel = gameModel;
        this.session = new ClientSession();
        this.lobbyModel = lobbyModel;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    @Override
    public void setController(ServerController controller) {

    }

    @Override
    public void start() {

    }

    @Override
    public void setNotificationController(ClientNotificationController notificationController) {

    }
    //TODO:Entry point della GUI. Estende Application, implementa ClientUi.
    // Crea ClientSession internamente. Salva tutte le dipendenze in campi statici perché JavaFX instanzia una seconda copia via reflection;
    // start(Stage) le legge e costruisce il router. Chiamare start() lancia la finestra e blocca il thread fino alla chiusura.


}

package it.polimi.ingsw.client.view.gui.screen;

import it.polimi.ingsw.client.view.gui.GuiContext;
import it.polimi.ingsw.client.view.gui.GuiNavigator;
import it.polimi.ingsw.client.view.gui.RefreshableScreen;
import it.polimi.ingsw.client.view.listeners.LobbyView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

import static it.polimi.ingsw.client.view.gui.GuiFxApp.*;

public class LobbyScreen implements LobbyView, RefreshableScreen {

    private final GuiContext ctx;
    private final GuiNavigator navigator;


    @FXML private VBox playersContainer;
    @FXML private Label statusLabel;

    // Nuovo costruttore per la Factory
    public LobbyScreen(GuiContext ctx, GuiNavigator navigator) {
        this.ctx = ctx;
        this.navigator = navigator;
    }

@FXML
    public void initialize() {
        ctx.notificationController().setLobbyView(this);
    }

    @Override
    public void onRoomUpdate(String notification, List<String> currentPlayers) {
        // Ignoriamo i parametri, ma dobbiamo forzare la UI a rileggere il LobbyModel
        Platform.runLater(this::refresh);
    }

    @Override
    public void onGameStarted() {
        ctx.notificationController().setLobbyView(null);
        Platform.runLater(() -> navigator.toInGame());
    }

    @Override
    public void onReturnToMatchmaking(String reason) {
        ctx.notificationController().setLobbyView(null);
        Platform.runLater(() -> navigator.toMatchmaking());
        // Nota: il motivo del ritorno andrebbe idealmente mostrato in MatchmakingScreen
    }

    @Override
    public void onError(String error) {
        // Aggiorna direttamente l'UI, quindi serve Platform.runLater
        Platform.runLater(() -> statusLabel.setText(error));
    }

    @Override
    public void onServerDisconnected(String reason) {
        ctx.notificationController().setLobbyView(null);
        Platform.runLater(() -> navigator.toDisconnected(reason));
    }

    @Override
    public void refresh() {
        ctx.lobbyModel().getReadLock().lock();
        try {
            playersContainer.getChildren().clear();
            List<String> players = ctx.lobbyModel().getLobbyPlayers();
            for (String player : players) {
                Label playerLabel = new Label(player);
                if (player.equals(ctx.session().getNickname()))
                    playerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");
                playersContainer.getChildren().add(playerLabel);
            }
            String notification = ctx.lobbyModel().getLobbyNotification();
            if (notification != null) statusLabel.setText(notification);
        } finally {
            ctx.lobbyModel().getReadLock().unlock();
        }
    }

    @FXML
    private void handleLeave() {
        ctx.controller().leaveGame();
    }
}
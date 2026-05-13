package it.polimi.ingsw.client.view.gui.screen;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DisconnectedScreen {

    @FXML private Label reasonLabel;

    private final String reason;

    public DisconnectedScreen(String reason) {
        this.reason = reason;
    }

    @FXML
    public void initialize() {
        reasonLabel.setText(reason != null ? reason : "Connessione persa.");
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }
}
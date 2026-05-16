package it.polimi.ingsw.client.view.gui.screen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InfoScreen {

    @FXML
    private Button closeButton;

    @FXML
    public void initialize() {
        // Il contenuto è interamente gestito in modo strutturato tramite FXML
    }

    @FXML
    private void onClose() {
        if (closeButton != null && closeButton.getScene() != null) {
            ((Stage) closeButton.getScene().getWindow()).close();
        }
    }
}
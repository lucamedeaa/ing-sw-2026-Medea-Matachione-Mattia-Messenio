package it.polimi.ingsw.client.view.gui.screen;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the disconnected screen.
 */
public class DisconnectedScreen {

    @FXML private Label reasonLabel;

    private final String reason;

    /**
     * Creates a disconnected screen controller.
     *
     * @param reason reason to show to the user
     */
    public DisconnectedScreen(String reason) {
        this.reason = reason;
    }

    /**
     * Initializes the disconnection reason label.
     */
    @FXML
    public void initialize() {
        reasonLabel.setText(reason != null ? reason : "Connection lost");
    }

    @FXML
    private void onExit() {
        Platform.exit();
        System.exit(0);
    }
}

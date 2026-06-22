package it.polimi.ingsw.client.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.List;

/**
 * FXML controller for the in-game log panel.
 */
public class LogPanelController {
    @FXML
    private TextArea logArea;

    /**
     * Appends new log messages to the log area.
     *
     * @param newLogs log messages to append
     */
    public void render(List<String> newLogs) {
        if (newLogs.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        for (String log : newLogs) {
            sb.append(stripAnsi(log)).append("\n");
        }
        logArea.appendText(sb.toString());
    }

    /**
     * Appends an error message to the log area.
     *
     * @param error error text
     */
    public void appendError(String error) {
        logArea.appendText("ERROR: " + stripAnsi(error) + "\n");
    }

    private String stripAnsi(String text) {
        if (text == null) return "";
        // Regex that strips ANSI color codes
        return text.replaceAll("\\u001B\\[[;\\d]*m", "");
    }
}


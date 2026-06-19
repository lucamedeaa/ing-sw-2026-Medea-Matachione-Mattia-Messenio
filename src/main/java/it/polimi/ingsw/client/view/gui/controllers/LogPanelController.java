package it.polimi.ingsw.client.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.List;

public class LogPanelController {
    @FXML
    private TextArea logArea;

    public void render(List<String> newLogs) {
        if (newLogs.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        for (String log : newLogs) {
            sb.append(stripAnsi(log)).append("\n");
        }
        logArea.appendText(sb.toString());
    }

    public void appendError(String error) {
        logArea.appendText("ERRORE: " + stripAnsi(error) + "\n");
    }

    private String stripAnsi(String text) {
        if (text == null) return "";
        // Regex that strips ANSI color codes
        return text.replaceAll("\\u001B\\[[;\\d]*m", "");
    }
}



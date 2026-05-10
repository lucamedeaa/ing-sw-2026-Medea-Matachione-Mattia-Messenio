package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class PlayersPanelController {
    private InGameScreen parentScreen;
    @FXML
    private VBox playersList;

    public void setParentScreen(InGameScreen parent) {
        this.parentScreen = parent;
    }

    public void refresh(GameModel model, String selfNickname) {
        playersList.getChildren().clear();

        for (PlayerSnapshot player : model.getPlayers().values()) {
            String nick = player.getNickname();
            Button pBtn = new Button(nick + " (" + player.getPrestige() + " VP)");

            // Stile differente se è il giocatore visualizzato al momento
            if (nick.equals(parentScreen.getViewedPlayer())) {
                pBtn.getStyleClass().add("active-player-tab");
            }

            pBtn.setOnAction(e -> parentScreen.setViewedPlayer(nick));
            playersList.getChildren().add(pBtn);
        }
    }
}
package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.model.snapshot.PlayerSnapshot;
import it.polimi.ingsw.client.view.gui.GuiAssetManager;
import it.polimi.ingsw.client.view.gui.screen.InGameScreen;
import it.polimi.ingsw.server.model.enums.TotemColor;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PlayersPanelController {
    private InGameScreen parentScreen;
    @FXML private VBox playersList;

    public void setParentScreen(InGameScreen parent) {
        this.parentScreen = parent;
    }

    public void refresh(GameModel model, String selfNickname) {
        playersList.getChildren().clear();

        String activePlayer = model.getActivePlayer();

        for (PlayerSnapshot player : model.getPlayers().values()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 10, 8, 10));

            // Icona del Totem
            ImageView totemImg = new ImageView(GuiAssetManager.getTotemImageFull(player.getTotemColor()));
            totemImg.setFitHeight(45);
            totemImg.setPreserveRatio(true);

            if (player.getNickname().equals(activePlayer)) {
                DropShadow glow = new DropShadow();
                glow.setColor(getGlowColor(player.getTotemColor()));
                glow.setRadius(20);
                glow.setSpread(0.5);
                totemImg.setEffect(glow);
            }

            // Testi: Nome + Statistiche
            VBox info = new VBox(2);

            String displayName = player.getNickname().toUpperCase();
            if (player.getNickname().equals(selfNickname)) {
                displayName += " (YOU)";
            }
            Label name = new Label(displayName);
            name.setStyle("-fx-text-fill: white; -fx-font-family: 'MedievalSharp'; -fx-font-size: 18px;");

            if (player.getNickname().equals(selfNickname)) {
                name.setStyle("-fx-text-fill: #ffd700; -fx-font-family: 'MedievalSharp'; -fx-font-size: 18px; -fx-font-weight: bold;");
            }

            Label stats = new Label(player.getPrestige() + " PP  |  " + player.getFood() + " Cibo");
            stats.setStyle("-fx-text-fill: #e67e22; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 13px;");

            info.getChildren().addAll(name, stats);
            row.getChildren().addAll(totemImg, info);

            // Evidenziazione se è il giocatore attualmente selezionato per guardare la tribù
            if (player.getNickname().equals(parentScreen.getViewedPlayer())) {
                row.setStyle("-fx-background-color: rgba(230, 126, 34, 0.25); -fx-border-color: #e67e22; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
            } else {
                row.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-border-color: transparent;");
            }

            // Al clic, aggiorna il giocatore in focus
            row.setOnMouseClicked(e -> parentScreen.setViewedPlayer(player.getNickname()));

            playersList.getChildren().add(row);
        }
    }

    private Color getGlowColor(TotemColor color) {
        if (color == null) return Color.WHITE;
        return switch (color) {
            case WHITE -> Color.WHITE;
            case BLUE -> Color.DEEPSKYBLUE;         // Più visibile del blu standard su sfondi scuri
            case YELLOW -> Color.LIGHTYELLOW;
            case BLACK -> Color.MEDIUMPURPLE;
            case ORANGE -> Color.ORANGE;
            default -> Color.GOLD;
        };
    }
}
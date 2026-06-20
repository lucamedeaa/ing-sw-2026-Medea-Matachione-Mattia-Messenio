package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GuiAssetManager;
import it.polimi.ingsw.client.view.gui.viewstate.PlayerInfo;
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

import java.util.List;

/**
 * FXML controller for the player summary panel.
 */
public class PlayersPanelController {
    private ViewedPlayerHost parentScreen;
    @FXML private VBox playersList;
    @FXML private Label roundEraLabel;

    /**
     * Sets the host screen that tracks the currently viewed player.
     *
     * @param parent host screen
     */
    public void setParentScreen(ViewedPlayerHost parent) {
        this.parentScreen = parent;
    }

    /**
     * Renders player rows and the current round/era label.
     *
     * @param players players to render
     * @param selfNickname nickname of the local player
     * @param activePlayer nickname of the active player
     * @param viewedPlayer nickname currently shown in the tribe panel
     * @param round current round
     * @param era current era
     */
    public void render(List<PlayerInfo> players, String selfNickname, String activePlayer, String viewedPlayer, int round, int era) {
        roundEraLabel.setText("Round " + round + " - Era " + era);
        playersList.getChildren().clear();
        for (PlayerInfo player : players) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 10, 8, 10));

            ImageView totemImg = new ImageView(
                    GuiAssetManager.getTotemImageFull(player.totemColor()));
            totemImg.setFitHeight(45);
            totemImg.setPreserveRatio(true);

            if (player.nickname().equals(activePlayer)) {
                DropShadow glow = new DropShadow();
                glow.setColor(getGlowColor(player.totemColor()));
                glow.setRadius(20);
                glow.setSpread(0.5);
                totemImg.setEffect(glow);
            }

            VBox info = new VBox(2);
            String displayName = player.nickname().toUpperCase()
                    + (player.nickname().equals(selfNickname) ? " (YOU)" : "");
            Label name = new Label(displayName);
            name.getStyleClass().add(player.nickname().equals(selfNickname)
                    ? "player-name-self" : "player-name");

            Label stats = new Label(player.prestige() + " PP  |  " + player.food() + " Cibo");
            stats.getStyleClass().add("player-stats-label");

            info.getChildren().addAll(name, stats);
            row.getChildren().addAll(totemImg, info);

            row.getStyleClass().add(player.nickname().equals(viewedPlayer)
                    ? "player-row-viewed" : "player-row");

            row.setOnMouseClicked(e -> parentScreen.setViewedPlayer(player.nickname()));
            playersList.getChildren().add(row);
        }
    }

    private Color getGlowColor(TotemColor color) {
        if (color == null) return Color.WHITE;
        return switch (color) {
            case WHITE -> Color.WHITE;
            case BLUE -> Color.DEEPSKYBLUE;
            case YELLOW -> Color.LIGHTYELLOW;
            case BLACK -> Color.MEDIUMPURPLE;
            case ORANGE -> Color.ORANGE;
            default -> Color.GOLD;
        };
    }
}

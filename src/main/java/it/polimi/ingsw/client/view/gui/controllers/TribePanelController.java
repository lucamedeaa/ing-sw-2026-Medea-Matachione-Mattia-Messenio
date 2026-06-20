package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.view.gui.GuiAssetManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;

/**
 * FXML controller for the tribe card panel.
 */
public class TribePanelController {

    @FXML private Label tribeOwnerLabel;
    @FXML private HBox cardsContainer;

    /**
     * Renders the selected player's tribe cards.
     *
     * @param cardIds card identifiers in the tribe
     * @param viewedPlayer nickname of the displayed player
     */
    public void render(List<Integer> cardIds, String viewedPlayer) {
        // already on the JavaFX thread — no Platform.runLater needed
        cardsContainer.getChildren().clear();
        tribeOwnerLabel.setText("TRIBE OF " + viewedPlayer.toUpperCase());
        for (Integer id : cardIds) {
            Image img = GuiAssetManager.getCardImage(id);
            if (img != null) {
                ImageView iv = new ImageView(img);
                iv.setPreserveRatio(true);
                iv.setFitHeight(160);
                iv.setOnMouseEntered(e -> iv.setTranslateY(-10));
                iv.setOnMouseExited(e -> iv.setTranslateY(0));
                StackPane wrapper = new StackPane(iv);
                wrapper.getStyleClass().add("card-view-wrapper");
                cardsContainer.getChildren().add(wrapper);
            }
        }
    }
}

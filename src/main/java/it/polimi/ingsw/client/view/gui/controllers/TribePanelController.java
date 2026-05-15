package it.polimi.ingsw.client.view.gui.controllers;

import it.polimi.ingsw.client.model.GameModel;
import it.polimi.ingsw.client.view.gui.GuiAssetManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;

public class TribePanelController {

    @FXML private Label tribeOwnerLabel;
    @FXML private HBox cardsContainer;

    public void refresh(GameModel model, String viewedPlayer) {
        Platform.runLater(() -> {
            cardsContainer.getChildren().clear();
            tribeOwnerLabel.setText("TRIBÙ DI " + viewedPlayer.toUpperCase());

            List<Integer> tribeCardIds = model.getTribes().get(viewedPlayer);
            if (tribeCardIds != null) {
                for (Integer id : tribeCardIds) {
                    Image img = GuiAssetManager.getCardImage(id);
                    if (img != null) {
                        ImageView iv = new ImageView(img);
                        iv.setPreserveRatio(true);
                        iv.setFitHeight(160);

                        // Effetto hover per esplorazione piacevole
                        iv.setOnMouseEntered(e -> iv.setTranslateY(-10));
                        iv.setOnMouseExited(e -> iv.setTranslateY(0));

                        StackPane wrapper = new StackPane(iv);
                        wrapper.getStyleClass().add("card-view-wrapper");
                        cardsContainer.getChildren().add(wrapper);
                    }
                }
            }
        });
    }
}
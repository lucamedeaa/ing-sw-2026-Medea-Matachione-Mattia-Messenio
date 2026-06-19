package it.polimi.ingsw.client.view.gui.controllers.board;

import it.polimi.ingsw.client.view.gui.GuiAssetManager;
import it.polimi.ingsw.server.model.enums.TotemColor;
import javafx.beans.property.DoubleProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CardNodeFactory {
    private final DoubleProperty cardWidthProp;

    public CardNodeFactory(DoubleProperty cardWidthProp) {
        this.cardWidthProp = cardWidthProp;
    }

    public DoubleProperty getCardWidthProp() { return cardWidthProp; }

    /** Pure visual card node — no click handler attached. */
    public StackPane createCardContainer(int cardId) {
        Image img = GuiAssetManager.getCardImage(cardId);
        if (img == null) return null;
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(false); iv.setSmooth(true);
        iv.fitWidthProperty().bind(cardWidthProp);
        iv.fitHeightProperty().bind(cardWidthProp.multiply(1.62)); // 1.62 = card aspect ratio (height / width)
        StackPane pane = new StackPane(iv);
        pane.setUserData(cardId);
        bindContainer(pane);
        return pane;
    }

    /** Tile node with a transparent overlay on top — returns both. */
    public record TileNode(StackPane container, Pane overlay) {}

    public TileNode createTileNode(String tileCode) {
        Image img = GuiAssetManager.getTileImage(tileCode);

        if (img == null) return null;
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(false); iv.setSmooth(true);
        iv.fitWidthProperty().bind(cardWidthProp);
        iv.fitHeightProperty().bind(cardWidthProp.multiply(1.62));
        StackPane container = new StackPane(iv);
        bindContainer(container);
        Pane overlay = new Pane();

        overlay.setMouseTransparent(true);
        overlay.minWidthProperty().bind(container.widthProperty());
        overlay.maxWidthProperty().bind(container.widthProperty());
        overlay.minHeightProperty().bind(container.heightProperty());
        overlay.maxHeightProperty().bind(container.heightProperty());
        container.getChildren().add(overlay);

        return new TileNode(container, overlay);
    }

   /** Totem image node. */
    public ImageView createTotemView(TotemColor color) {
        Image img = GuiAssetManager.getTotemImage(color);
        if (img == null) return null;
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind(cardWidthProp.multiply(0.38));
        iv.layoutXProperty().bind(cardWidthProp.subtract(iv.fitWidthProperty()).divide(2));
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(20,20,20,0.85));
        shadow.setRadius(4); shadow.setSpread(0.4); shadow.setOffsetY(2);
        iv.setEffect(shadow);
        return iv;
    }

    private void bindContainer(StackPane pane) {
        pane.minWidthProperty().bind(cardWidthProp);
        pane.maxWidthProperty().bind(cardWidthProp);
        pane.prefWidthProperty().bind(cardWidthProp);
        pane.minHeightProperty().bind(cardWidthProp.multiply(1.62));
        pane.maxHeightProperty().bind(cardWidthProp.multiply(1.62));
        pane.prefHeightProperty().bind(cardWidthProp.multiply(1.62));
    }
   /** Back-of-deck node for the main deck. */
    public StackPane createDeckBackContainer(int era) {
        Image img = GuiAssetManager.getDeckBackImage(era);
        return buildDeckPane(img);
    }

    /** Back-of-deck node for the building decks. */
    public StackPane createBuildingDeckBackContainer(int era) {
        Image img = GuiAssetManager.getBuildingDeckBackImage(era);
        return buildDeckPane(img);
    }

    private StackPane buildDeckPane(Image img) {
        if (img == null) return null;

        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(false);
        iv.setSmooth(true);
        iv.fitWidthProperty().bind(cardWidthProp);
        iv.fitHeightProperty().bind(cardWidthProp.multiply(1.62));

        StackPane pane = new StackPane(iv);
        bindContainer(pane);
        return pane;
    }
}

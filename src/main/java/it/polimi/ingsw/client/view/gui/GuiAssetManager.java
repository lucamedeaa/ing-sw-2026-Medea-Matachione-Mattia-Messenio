package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.server.model.enums.TotemColor;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and caches JavaFX images used by the GUI.
 */
public class GuiAssetManager {

    // Cache so each image is loaded from disk only once
    private static final Map<Integer, Image> cardCache = new HashMap<>();
    private static final Map<String, Image> tileCache = new HashMap<>();
    private static final Map<TotemColor, Image> totemCache = new HashMap<>();
    private static final Map<TotemColor, Image> totemFullCache = new HashMap<>();
    private static final Map<Integer, Image> cardBackCache = new HashMap<>();
    private static final Map<Integer, Image> buildingBackCache = new HashMap<>();

    /**
     * Returns the image for a card.
     *
     * @param cardId card identifier
     * @return loaded card image, or null if the resource is missing
     */
    public static Image getCardImage(int cardId) {
        if (cardCache.containsKey(cardId)) {
            return cardCache.get(cardId);
        }

        // Path is resolved from the resources root
        String path = "/images/cards/" + cardId + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("Critical error: image not found for card ID " + cardId + " at path " + path);
            return null;
        }

        Image image = new Image(is);
        cardCache.put(cardId, image);
        return image;
    }


    /**
     * Returns the image for a board tile.
     *
     * @param tileName resource name of the tile
     * @return loaded tile image, or null if the resource is missing
     */
    public static Image getTileImage(String tileName) {
        if (tileCache.containsKey(tileName)) {
            return tileCache.get(tileName);
        }

        String path = "/images/tiles/" + tileName + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("Critical error: tile image not found at path " + path);
            return null;
        }

        Image image = new Image(is);
        tileCache.put(tileName, image);
        return image;
    }

    /**
     * Returns the compact totem image for a color.
     *
     * @param color totem color
     * @return loaded totem image, or null if color or resource is missing
     */
    public static Image getTotemImage(TotemColor color) {
        if (color == null) {
            return null;
        }

        if (totemCache.containsKey(color)) {
            return totemCache.get(color);
        }

        String path = "/images/totem/" + color + "_COLOR.png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("Critical error: totem image not found for color " + color + " at path " + path);
            return null;
        }

        Image image = new Image(is);
        totemCache.put(color, image);
        return image;
    }

    /**
     * Returns the full-size totem image for a color.
     *
     * @param color totem color
     * @return loaded totem image, or null if color or resource is missing
     */
    public static Image getTotemImageFull(TotemColor color) {
        if (color == null) {
            return null;
        }

        if (totemFullCache.containsKey(color)) {
            return totemFullCache.get(color);
        }

        // Build path as TOTEM_<COLOR>
        String path = "/images/totem/TOTEM_" + color.name() + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("Critical error: totem image not found for color " + color.name() + " at path " + path);
            return null;
        }

        Image image = new Image(is);
        totemFullCache.put(color, image);
        return image;
    }

    /**
     * Returns the main deck back image for an era.
     *
     * @param era era number
     * @return loaded deck back image, or null if the resource is missing
     */
    public static Image getDeckBackImage(int era) {
        if (cardBackCache.containsKey(era)) return cardBackCache.get(era);

        String path = "/images/tiles/BACK_ERA_" + era + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("Deck back image not found for era " + era + " at path " + path);
            return null;
        }

        Image image = new Image(is);
        cardBackCache.put(era, image);
        return image;
    }

    /**
     * Returns the building deck back image for an era.
     *
     * @param era era number
     * @return loaded building deck back image, or null if the resource is missing
     */
    public static Image getBuildingDeckBackImage(int era) {
        if (buildingBackCache.containsKey(era)) return buildingBackCache.get(era);

        String path = "/images/tiles/BACK_BUILDING_ERA_" + era + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("Building deck back image not found for era " + era + " at path " + path);
            return null;
        }

        Image image = new Image(is);
        buildingBackCache.put(era, image);
        return image;

    }
}

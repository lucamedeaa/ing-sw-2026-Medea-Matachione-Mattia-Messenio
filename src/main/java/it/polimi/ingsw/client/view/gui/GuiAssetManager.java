package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.server.model.enums.TotemColor;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GuiAssetManager {

    // Cache so each image is loaded from disk only once
    private static final Map<Integer, Image> cardCache = new HashMap<>();
    private static final Map<String, Image> tileCache = new HashMap<>();
    private static final Map<TotemColor, Image> totemCache = new HashMap<>();
    private static final Map<TotemColor, Image> totemFullCache = new HashMap<>();
    private static final Map<Integer, Image> cardBackCache = new HashMap<>();
    private static final Map<Integer, Image> buildingBackCache = new HashMap<>();


    public static Image getCardImage(int cardId) {
        if (cardCache.containsKey(cardId)) {
            return cardCache.get(cardId);
        }

        // Path is resolved from the resources root
        String path = "/images/cards/" + cardId + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("ERRORE CRITICO: Immagine non trovata per l'ID " + cardId + " al percorso " + path);
            return null;
        }

        Image image = new Image(is);
        cardCache.put(cardId, image);
        return image;
    }


    public static Image getTileImage(String tileName) {
        if (tileCache.containsKey(tileName)) {
            return tileCache.get(tileName);
        }

        String path = "/images/tiles/" + tileName + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("ERRORE CRITICO: Immagine tile non trovata al percorso " + path);
            return null;
        }

        Image image = new Image(is);
        tileCache.put(tileName, image);
        return image;
    }

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
            System.err.println("ERRORE CRITICO: Immagine totem non trovata per il colore " + color + " al percorso " + path);
            return null;
        }

        Image image = new Image(is);
        totemCache.put(color, image);
        return image;
    }

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
            System.err.println("ERRORE CRITICO: Immagine totem non trovata per il colore " + color.name() + " al percorso " + path);
            return null;
        }

        Image image = new Image(is);
        totemFullCache.put(color, image);
        return image;
    }

    public static Image getDeckBackImage(int era) {
        if (cardBackCache.containsKey(era)) return cardBackCache.get(era);

        String path = "/images/tiles/BACK_ERA_" + era + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("Dorso non trovato per Era " + era + " al percorso " + path);
            return null;
        }

        Image image = new Image(is);
        cardBackCache.put(era, image);
        return image;
    }

    public static Image getBuildingDeckBackImage(int era) {
        if (buildingBackCache.containsKey(era)) return buildingBackCache.get(era);

        String path = "/images/tiles/BACK_BUILDING_ERA_" + era + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("Dorso non trovato per Building Era " + era + " al percorso " + path);
            return null;
        }

        Image image = new Image(is);
        buildingBackCache.put(era, image);
        return image;

    }
}
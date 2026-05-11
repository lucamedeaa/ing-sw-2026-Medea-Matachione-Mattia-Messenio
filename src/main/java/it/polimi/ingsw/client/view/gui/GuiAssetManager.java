package it.polimi.ingsw.client.view.gui;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GuiAssetManager {

    // Cache per evitare di allocare nuova memoria per immagini già caricate
    private static final Map<Integer, Image> cardCache = new HashMap<>();
    private static final Map<String, Image> tileCache = new HashMap<>();

    public static Image getCardImage(int cardId) {
        if (cardCache.containsKey(cardId)) {
            return cardCache.get(cardId);
        }

        // Il path parte dalla radice di 'resources'
        String path = "/images/cards/" + cardId + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("ERRORE CRITICO: Immagine non trovata per l'ID " + cardId + " al percorso " + path);
            return null; // Oppure ritorna un'immagine di errore/placeholder
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
}
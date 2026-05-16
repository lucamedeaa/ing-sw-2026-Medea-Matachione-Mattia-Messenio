package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.server.model.enums.TotemColor;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GuiAssetManager {

    // Cache per evitare di allocare nuova memoria per immagini già caricate
    private static final Map<Integer, Image> cardCache = new HashMap<>();
    private static final Map<String, Image> tileCache = new HashMap<>();
    private static final Map<TotemColor, Image> totemCache = new HashMap<>();
    private static final Map<TotemColor, Image> totemFullCache = new HashMap<>();
    private static final Map<Integer, Image> cardBackCache = new HashMap<>();


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

    public static Image getTotemImage(TotemColor color) {
        if (color == null) {
            return null;
        }

        // Normalizziamo il nome per sicurezza (es. da "Red" a "RED")
        

        if (totemCache.containsKey(color)) {
            return totemCache.get(color);
        }

        // Assicurati che il path corrisponda alla cartella reale nei tuoi resources
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

        // Costruisce il nuovo path con il prefisso TOTEM_ e il nome del colore
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

    public static Image getCardBackImage(int era) {
        if (cardBackCache.containsKey(era)) return cardBackCache.get(era);

        String path = "/images/tiles/era_" + era + ".png";
        InputStream is = GuiAssetManager.class.getResourceAsStream(path);

        if (is == null) {
            System.err.println("Dorso non trovato per Era " + era + " al percorso " + path);
            return null;
        }

        Image image = new Image(is);
        cardBackCache.put(era, image);
        return image;
    }
}
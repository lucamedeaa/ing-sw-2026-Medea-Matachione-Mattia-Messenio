package it.polimi.ingsw.client.view.tui.card;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CardNameMapper {
    private static Map<Integer, CardInfo> registry = new HashMap<>();

    static {
        loadFromJson();
    }

    private static void loadFromJson() {
        // Legge il file dalla cartella resources
        try (InputStream is = CardNameMapper.class.getResourceAsStream("/cards.json")) {
            if (is != null) {
                ObjectMapper mapper = new ObjectMapper();
                // Usa un TypeReference per mappare direttamente il JSON su Map<Integer, CardInfo>
                registry = mapper.readValue(is, new TypeReference<Map<Integer, CardInfo>>() {});
            } else {
                System.err.println("[WARNING] The cards.json file was not found in the classpath!");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Unable to parse cards.json: " + e.getMessage());
        }
    }

    public static CardInfo getCard(int id) {
        return registry.getOrDefault(id, new CardInfo("Card#" + id, "Unknown", 0, "", "", ""));
    }

    public static boolean isEvent(int id) {
        return getCard(id).type().equals("Event");
    }

    public static boolean isBuilding(int id) {
        return getCard(id).type().equals("Building");
    }
}
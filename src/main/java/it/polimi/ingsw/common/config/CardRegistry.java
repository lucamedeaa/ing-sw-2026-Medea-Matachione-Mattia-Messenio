package it.polimi.ingsw.common.config;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;
import java.util.Collections;

public class CardRegistry {
    private static final Map<Integer, CardInfo> CARDS;

    static {
        try (InputStream is = CardRegistry.class.getResourceAsStream("/cards.json")) {
            if (is == null) throw new IllegalStateException("cards.json not found!");
            ObjectMapper mapper = new ObjectMapper();
            CARDS = Collections.unmodifiableMap(mapper.readValue(is, new TypeReference<>() {}));
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Card loading error: " + e.getMessage());
        }
    }

    public static CardInfo getCard(int id) {
        CardInfo def = CARDS.get(id);
        if (def == null) throw new IllegalArgumentException("Card not found: " + id);
        return def;
    }
}
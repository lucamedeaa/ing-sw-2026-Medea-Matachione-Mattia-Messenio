package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.card.character.Hunter;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HunterTest extends ModelTest {

    @Test
    @DisplayName("First hunter WITH icon gives exactly 1 food")
    void firstHunterWithIconGivesOneFood() {
        Player p = newPlayer("Alice");
        // Updated Constructor: idcard, era, hasIcon
        p.addCard(new Hunter(10));

        assertEquals(1, p.getFood(),
                "The first hunter with an icon must give 1 food (only him in the tribe)");
    }

    @Test
    @DisplayName("Second hunter WITH icon counts all present hunters")
    void secondHunterWithIconCountsAll() {
        Player p = newPlayer("Alice");
        p.addCard(new Hunter(12)); // no food
        p.addCard(new Hunter(10));  // 2 hunters in tribe -> +2 food

        assertEquals(2, p.getFood());
    }

    @Test
    @DisplayName("Hunter without icon added after another gives no food")
    void addingNoIconHunterAfterIconHunterGivesNoFood() {
        Player p = newPlayer("Alice");
        p.addCard(new Hunter(10));  // +1 food
        int foodAfterFirst = p.getFood();

        p.addCard(new Hunter(12)); // should not give food

        assertEquals(foodAfterFirst, p.getFood());
    }
}
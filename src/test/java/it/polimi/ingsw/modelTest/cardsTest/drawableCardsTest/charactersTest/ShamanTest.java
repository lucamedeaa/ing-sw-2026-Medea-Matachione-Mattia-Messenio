package it.polimi.ingsw.modelTest.cardsTest.drawableCardsTest.charactersTest;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.drawableCards.characters.Shaman;
import it.polimi.ingsw.modelTest.ModelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShamanTest extends ModelTest {

    @Test
    @DisplayName("Shaman adds correct number of stars")
    void shamanAddsStars() {
        Player p = newPlayer("Bob");
        // Updated Constructor: idcard, era, starsCount
        p.addCard(new Shaman(28, 1, 2));
        p.addCard(new Shaman(29, 1, 1));

        assertEquals(3, p.getStarsNumber(), "Should sum the stars from all shamans");
    }
}
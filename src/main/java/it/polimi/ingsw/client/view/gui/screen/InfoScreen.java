package it.polimi.ingsw.client.view.gui.screen;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

// Dialog modale con guida statica alle carte del gioco.
// Nessuna dipendenza da model o server — contenuto fisso.
// Aperto da InGameScreen come Stage con Modality.APPLICATION_MODAL.
// Si chiude con il bottone "Chiudi" o con la X della finestra.
public class InfoScreen {

    @FXML private TextArea contentArea;

    private static final String CONTENT =
            """
   ── CHARACTER TYPES ─────────────────────────────────────────────
     Builder    Grants food discount for buildings, gives PP at game end
                e.g. -1f discount, +2pp final
     Hunter     sym:✓ grants instant food equal to Hunters in tribe
                Hunt event: grants food per Hunter in tribe
     Artist     No direct effect — score via CavePaintings
     Shaman     ★x1 / ★x2 / ★x3  ritual stars
     Collector  Provides 3 food discount during the Sustenance event
     Inventor   Has an icon — matching pairs score ONLY with InventorPair
                Icons: Spearhead Leather Bread Canoe Mortar
                       Rope Flute Statue Fishhook Necklace

   ── EVENTS ──────────────────────────────────────────────────────
     CavePaintings   Thresholds and PP rewards depend on the specific card
     Hunt            Grants food based on Hunter count
     ShamanicRitual  Highest stars gain PP, lowest lose PP (ties apply)
     Sustenance      -1f per character (Collectors discount)
                     if food insufficient: -N pp per missing food

   ── BUILDINGS ───────────────────────────────────────────────────
     Bought by spending food; grant prestige at the end of the game.
     Era 1 (3-5f)   RitualShield  DiverseSet   InventorPair
                    TurnBonus     FoodDsc(Art) FoodDsc(Col)
     Era 2 (5-7f)   ArtistFood    BuildrMastery RitualStars
                    DblPrestige   FoodDsc(Inv) SetScorer
                    HunterBonus
     Era 3 (6-10f)  VictoryPoints LatePurchase
                    ClassScorer(Inv/Art/Sha/Hun/Col/Bui)
   """;

    @FXML
    public void initialize() {
        contentArea.setText(CONTENT);
        contentArea.setEditable(false);
    }

    @FXML
    private void onClose() {
        ((Stage) contentArea.getScene().getWindow()).close();
    }
}
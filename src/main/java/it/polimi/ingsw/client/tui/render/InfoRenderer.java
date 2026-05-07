package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.OutputPort;

public class InfoRenderer {
    private final OutputPort out;

    public InfoRenderer(OutputPort out) {
        this.out = out;
    }

    public void render() {
        out.clearScreen();
        System.out.print("\033[H\033[2J");
        System.out.flush();
        out.print("┌─────────────────────────────────────────────────────────────┐");
        out.print("│                  MESOS — CARD REFERENCE                     │");
        out.print("└─────────────────────────────────────────────────────────────┘");
        out.print("── CHARACTER TYPES ────────────────────────────────────────────");
        out.print("  Builder    Grants food discount for buildings, gives PP at game end");
        out.print("             e.g. -1f discount, +2pp final");
        out.print("  Hunter     sym:✓ grants instant food equal to Hunters in tribe");
        out.print("             Hunt event: grants food per Hunter in tribe");
        out.print("  Artist     No direct effect — score via CavePaintings");
        out.print("  Shaman     ★x1 / ★x2 / ★x3  ritual stars");
        out.print("  Collector  Provides 3 food discount during the Sustenance event");
        out.print("  Inventor   Has an icon — matching pairs score ONLY with InventorPair");
        out.print("             Icons: Spearhead Leather Bread Canoe Mortar");
        out.print("                    Rope Flute Statue Fishhook Necklace");
        out.print("── EVENTS ─────────────────────────────────────────────────────");
        out.print("  CavePaintings  Thresholds and PP rewards depend on the specific card");
        out.print("  Hunt           Grants food based on Hunter count");
        out.print("  ShamanicRitual Highest stars gain PP, lowest lose PP (ties apply)");
        out.print("  Sustenance     -1f per character (Collectors discount)");
        out.print("                 if food insufficient: -N pp per missing food");
        out.print("── BUILDINGS ──────────────────────────────────────────────────");
        out.print("  Bought by spending food; grant prestige at the end of the game.");
        out.print("  Era 1 (3-5f)  RitualShield  DiverseSet   InventorPair");
        out.print("                TurnBonus     FoodDsc(Art) FoodDsc(Col)");
        out.print("  Era 2 (5-7f)  ArtistFood    BuildrMastery RitualStars");
        out.print("                DblPrestige   FoodDsc(Inv) SetScorer");
        out.print("                HunterBonus");
        out.print("  Era 3 (6-10f) VictoryPoints LatePurchase");
        out.print("                ClassScorer(Inv/Art/Sha/Hun/Col/Bui)");
        out.print("── COMMANDS ───────────────────────────────────────────────────");
        out.print("  N              select action N from the list");
        out.print("  N <tile>       place totem on tile index");
        out.print("  N <row> <col>  take card  (row: 0=upper  1=lower)");
        out.print("  v <name>       view player's tribe");
        out.print("  i              open this reference guide");
        out.print("  q              return to game");
        System.out.print("  Press Q to return to game > ");
    }
}
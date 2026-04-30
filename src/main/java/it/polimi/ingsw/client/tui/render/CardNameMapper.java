package it.polimi.ingsw.client.tui.render;

public class CardNameMapper {

    public static String getName(int id) {
        if (isBuilding(id)) return buildingName(id);
        if (isEvent(id))    return eventName(id);
        return characterType(id);
    }

    /** Seconda riga del box: caratteristica specifica della carta. */
    public static String getDetail(int id) {
        if (isBuilding(id)) { int era = id <= 101 ? 1 : id <= 108 ? 2 : 3; return "Era " + era; }
        if (isEvent(id))    return eventDetail(id);
        return characterDetail(id);
    }

    public static boolean isEvent(int id)    { return id >= 53 && id <= 63; }
    public static boolean isBuilding(int id) { return id >= 96; }


    public static String characterType(int id) {
        if (id >= 1  && id <= 9)  return "Builder";
        if (id >= 10 && id <= 18) return "Hunter";
        if (id >= 19 && id <= 27) return "Artist";
        if (id >= 28 && id <= 34) return "Shaman";
        if (id >= 35 && id <= 38) return "Collector";
        if (id >= 39 && id <= 52) return "Inventor";
        if (id == 64 || id == 65 || id == 77 || id == 95)                        return "Artist";
        if (id == 66 || id == 67 || id == 68 || id == 84 || id == 93 || id == 94) return "Hunter";
        if (id == 69 || id == 70 || id == 82 || id == 83 || id == 88 || id == 89 || id == 90) return "Collector";
        if (id == 71 || id == 91 || id == 92)                                     return "Builder";
        if (id == 72 || id == 73 || id == 78 || id == 79 || id == 80 || id == 81) return "Inventor";
        if (id == 74 || id == 75 || id == 76 || id == 85 || id == 86 || id == 87) return "Shaman";
        return "Card#" + id;
    }


    private static String characterDetail(int id) {
        return switch (id) {
            // Builder(id, era, foodDiscount, pp)
            case 1  -> "-1f +2pp";
            case 2  -> "-2f +0pp";
            case 3  -> "-1f +3pp";
            case 4  -> "-1f +4pp";
            case 5  -> "-2f +1pp";
            case 6  -> "-2f +3pp";
            case 7  -> "-1f +5pp";
            case 8  -> "-2f +3pp";
            case 9  -> "-2f +2pp";
            case 71 -> "-1f +2pp";
            case 91 -> "-2f +1pp";
            case 92 -> "-1f +4pp";
            // Hunter(id, era, hasSymbol) TRUE=✓ FALSE=✗
            case 10, 11, 13, 16, 68, 84, 94 -> "sym: ✓";
            case 12, 14, 15, 17, 18, 66, 67, 93 -> "sym: ✗";
            // Shaman(id, era, stars)
            case 28, 75 -> "★ x1";
            case 29, 30, 31, 32, 74, 76, 85, 87 -> "★ x2";
            case 33, 34 -> "★ x3";
            case 86     -> "★ x1";
            // Collector — sempre -3 food
            case 35, 36, 37, 38, 69, 70, 82, 83, 88, 89, 90 -> "-3 food";
            // Inventor(id, era, icon)
            case 39, 72 -> "Spearhead";
            case 40, 45 -> "Leather";
            case 41, 52 -> "Bread";
            case 42, 73 -> "Canoe";
            case 43, 46 -> "Mortar";
            case 44, 78 -> "Rope";
            case 47, 79 -> "Flute";
            case 48, 50 -> "Statue";
            case 49, 80 -> "Fishhook";
            case 51, 81 -> "Necklace";
            default -> "";
        };
    }

    // ── Evento ───────────────────────────────────────────────────────────────

    private static String eventName(int id) {
        return switch (id) {
            case 53 -> "CvPnt I";
            case 54 -> "CvPnt II";
            case 55 -> "CvPnt III";
            case 56 -> "Hunt I";
            case 57 -> "Hunt II";
            case 58 -> "ShmRit I";
            case 59 -> "ShmRit II";
            case 60 -> "ShmRit III";
            case 61 -> "Sstnc I";
            case 62 -> "Sstnc II";
            case 63 -> "Sstnc III";
            default -> "Event#" + id;
        };
    }

    private static String eventDetail(int id) {
        return switch (id) {
            case 53 -> "≥1 art:-2pp";  // CavePaintings I:  <1art → -2pp, ≥2art → +1pp/art
            case 54 -> "≥2 art:-2pp";
            case 55 -> "≥3 art:-2pp";
            case 56 -> "+1f +1pp/hun"; // Hunt I
            case 57 -> "+1f +2pp/hun"; // Hunt II
            case 58 -> "top:+5 bot:-3"; // ShamanicRitual I
            case 59 -> "top:+10 bot:-5";
            case 60 -> "top:+15 bot:-7";
            case 61 -> "-1f/char";     // Sustenance I
            case 62, 63 -> "-1f/char";
            default -> "";
        };
    }

    // ── Edificio ─────────────────────────────────────────────────────────────

    private static String buildingName(int id) {
        return switch (id) {
            case 96  -> "RitualShield";
            case 97  -> "DiverseSet";
            case 98  -> "InventorPair";
            case 99  -> "TurnBonus";
            case 100 -> "FoodDsc(Art)";
            case 101 -> "FoddDsc(Col)";
            case 102 -> "ArtistFood";
            case 103 -> "BuliderMasty";
            case 104 -> "RitualStars";
            case 105 -> "DblRitual";
            case 106 -> "FoddDsc(Inv)";
            case 107 -> "SetScorer";
            case 108 -> "HunterBonus";
            case 109 -> "VictoryPts";
            case 110 -> "LatePurchase";
            case 111 -> "ClsScr(I)";
            case 112 -> "ClsScr(A)";
            case 113 -> "ClsScr(S)";
            case 114 -> "ClsScr(H)";
            case 115 -> "ClsScr(C)";
            case 116 -> "ClsScr(B)";
            default  -> "Bldg#" + id;
        };
    }
}
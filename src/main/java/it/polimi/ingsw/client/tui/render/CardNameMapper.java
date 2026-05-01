package it.polimi.ingsw.client.tui.render;

public class CardNameMapper {

    public static String getName(int id) {
        if (isBuilding(id)) return buildingName(id);
        if (isEvent(id))    return eventName(id);
        return characterType(id);
    }

    public static String getDetail(int id) {
        if (isBuilding(id)) {
            return "";
        }
        if (isEvent(id)) return eventDetail(id);
        return characterDetail(id);
    }

    public static int getEra(int id) {
        if (isBuilding(id)) {
            return id <= 101 ? 1 : id <= 108 ? 2 : 3;
        }
        return switch (id) {
            // ERA 1
            case 1, 2, 3, 91,           // Builder
                 10, 11, 12, 66, 67,    // Hunter
                 19, 20, 21, 64, 77,    // Artist
                 28, 29, 75, 85,        // Shaman
                 35, 36, 69, 88,        // Collector
                 39, 40, 41, 42, 43, 78, 79, // Inventor
                 53, 56, 58, 61         // Eventi
                    -> 1;

            // ERA 2
            case 4, 5, 6, 71,           // Builder
                 13, 14, 15, 68, 84, 93,// Hunter
                 22, 23, 24, 65,        // Artist
                 30, 31, 86, 87,        // Shaman
                 37, 70, 82, 89,        // Collector
                 44, 45, 46, 47, 48, 49, 50, 51, 52, 80, // Inventor
                 54, 57, 59, 62, 63     // Eventi
                    -> 2;

            // ERA 3
            case 7, 8, 9, 92,           // Builder
                 16, 17, 18, 94,        // Hunter
                 25, 26, 27, 95,        // Artist
                 32, 33, 34, 74, 76,    // Shaman
                 38, 83, 90,            // Collector
                 72, 73, 81,            // Inventor
                 55, 60                 // Eventi
                    -> 3;

            default -> 0; // Fallback di sicurezza
        };
    }

    public static String getExtra(int id) {
        if (!isBuilding(id)) return "";
        return switch (id) {
            case 96  -> "cost: 5f";
            case 97  -> "cost: 4f";
            case 98  -> "cost: 3f";
            case 99  -> "cost: 3f";
            case 100 -> "cost: 5f";
            case 101 -> "cost: 4f";
            case 102 -> "cost: 5f";
            case 103 -> "cost: 6f";
            case 104 -> "cost: 6f";
            case 105 -> "cost: 7f";
            case 106 -> "cost: 7f";
            case 107 -> "cost: 5f";
            case 108 -> "cost: 7f";
            case 109 -> "cost: 10f";
            case 110 -> "cost: 9f";
            case 111 -> "cost: 6f";
            case 112 -> "cost: 7f";
            case 113 -> "cost: 7f";
            case 114 -> "cost: 8f";
            case 115 -> "cost: 7f";
            case 116 -> "cost: 6f";
            default  -> "";
        };
    }

    public static String getExtra2(int id) {
        if (!isBuilding(id)) return "";
        return switch (id) {
            case 96  -> "+2 pp";
            case 97  -> "+3 pp";
            case 98  -> "+4 pp";
            case 99  -> "+3 pp";
            case 100 -> "+3 pp";
            case 101 -> "+4 pp";
            case 102 -> "+6 pp";
            case 103 -> "+4 pp";
            case 104 -> "+4 pp";
            case 105 -> "+0 pp";
            case 106 -> "+4 pp";
            case 107 -> "+6 pp";
            case 108 -> "+2 pp";
            case 109 -> "+0 pp";
            case 110 -> "+3 pp";
            case 111 -> "+6 pp";
            case 112 -> "+4 pp";
            case 113 -> "+4 pp";
            case 114 -> "+8 pp";
            case 115 -> "+6 pp";
            case 116 -> "+3 pp";
            default  -> "";
        };
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
        if (id == 64 || id == 65 || id == 77 || id == 95)                         return "Artist";
        if (id == 66 || id == 67 || id == 68 || id == 84 || id == 93 || id == 94) return "Hunter";
        if (id == 69 || id == 70 || id == 82 || id == 83 || id == 88 || id == 89 || id == 90) return "Collector";
        if (id == 71 || id == 91 || id == 92)                                      return "Builder";
        if (id == 72 || id == 73 || id == 78 || id == 79 || id == 80 || id == 81) return "Inventor";
        if (id == 74 || id == 75 || id == 76 || id == 85 || id == 86 || id == 87) return "Shaman";
        return "Card#" + id;
    }

    private static String characterDetail(int id) {
        return switch (id) {
            case 1          -> "-1f +2pp";
            case 2          -> "-2f +0pp";
            case 3          -> "-1f +3pp";
            case 4          -> "-1f +4pp";
            case 5          -> "-2f +1pp";
            case 6          -> "-2f +3pp";
            case 7          -> "-1f +5pp";
            case 8          -> "-2f +3pp";
            case 9          -> "-2f +2pp";
            case 71         -> "-1f +2pp";
            case 91         -> "-2f +1pp";
            case 92         -> "-1f +4pp";
            case 10, 11, 13, 16, 68, 84, 94         -> "sym: ✓";
            case 12, 14, 15, 17, 18, 66, 67, 93     -> "sym: ✗";
            case 19, 20, 21, 22, 23, 24, 25, 26, 27,
                 64, 65, 77, 95                      -> "";
            case 28, 75     -> "★ x1";
            case 86         -> "★ x1";
            case 29, 30, 31, 32, 74, 76, 85, 87     -> "★ x2";
            case 33, 34     -> "★ x3";
            case 35, 36, 37, 38, 69, 70,
                 82, 83, 88, 89, 90                  -> "-3 food";
            case 39, 72     -> "Spearhead";
            case 40, 45     -> "Leather";
            case 41, 52     -> "Bread";
            case 42, 73     -> "Canoe";
            case 43, 46     -> "Mortar";
            case 44, 78     -> "Rope";
            case 47, 79     -> "Flute";
            case 48, 50     -> "Statue";
            case 49, 80     -> "Fishhook";
            case 51, 81     -> "Necklace";
            default         -> "";
        };
    }

    private static String eventName(int id) {
        return switch (id) {
            case 53 -> "CavePaintings I";
            case 54 -> "CavePaintings II";
            case 55 -> "CavePaintings III";
            case 56 -> "Hunt I";
            case 57 -> "Hunt II";
            case 58 -> "ShamanicRitual I";
            case 59 -> "ShamanicRitual II";
            case 60 -> "ShamanicRitual III";
            case 61 -> "Sustenance I";
            case 62 -> "Sustenance II";
            case 63 -> "Sustenance III";
            default -> "Event#" + id;
        };
    }

    private static String eventDetail(int id) {
        return switch (id) {
            case 53 -> "≥1a +1/a";
            case 54 -> "≥2a +2/a";
            case 55 -> "≥3a +3/a";
            case 56 -> "+1f +1/hun";
            case 57 -> "+1f +2/hun";
            case 58 -> "+5/-3pp";
            case 59 -> "+10/-5pp";
            case 60 -> "+15/-7pp";
            case 61, 62, 63 -> "-1f/char";
            default -> "";
        };
    }

    private static String buildingName(int id) {
        return switch (id) {
            case 96  -> "RitualShield";
            case 97  -> "DiverseSet";
            case 98  -> "InventorPair";
            case 99  -> "TurnBonus";
            case 100 -> "FoodDiscount(Art)";
            case 101 -> "FoodDiscount(Col)";
            case 102 -> "ArtistFood";
            case 103 -> "BuilderMastery";
            case 104 -> "RitualStars";
            case 105 -> "DoublePrestige";
            case 106 -> "FoodDiscount(Inv)";
            case 107 -> "SetScorer";
            case 108 -> "HunterBonus";
            case 109 -> "VictoryPoints";
            case 110 -> "LatePurchase";
            case 111 -> "ClassScorer(Inv)";
            case 112 -> "ClassScorer(Art)";
            case 113 -> "ClassScorer(Sha)";
            case 114 -> "ClassScorer(Hun)";
            case 115 -> "ClassScorer(Col)";
            case 116 -> "ClassScorer(Bui)";
            default  -> "Building#" + id;
        };
    }
}
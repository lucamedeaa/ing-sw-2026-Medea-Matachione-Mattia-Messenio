package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.common.config.CardInfo;
import it.polimi.ingsw.common.config.CardRegistry;

import java.util.List;

/**
 * Utility renderer for compact ASCII card boxes used by TUI screens.
 */
public class CardBoxRenderer {

    private static final String RESET = "AnsiColor.RESET";

    /**
     * Returns the ANSI color associated with the card type.
     *
     * @param id card identifier, or null for an empty slot
     * @return ANSI color code
     */
    public static String ansiColor(Integer id) {
        if (id == null) return ColorAnsi.GRAY;
        CardInfo info = CardRegistry.getCard(id);

        if (info.type().equals("Event")) {
            if (info.era() == 3 && (info.name().contains("Sustenance") || info.name().contains("ShamanicRitual"))) {
                return ColorAnsi.BLUE_BOLD;
            }
            return ColorAnsi.YELLOW;
        }
        if (info.type().equals("Building")) return ColorAnsi.CYAN;

        return switch (info.type()) {
            case "Builder"   -> ColorAnsi.GREEN_BOLD;
            case "Hunter"    -> ColorAnsi.RED;
            case "Artist"    -> ColorAnsi.MAGENTA;
            case "Shaman"    -> ColorAnsi.WHITE_BOLD;
            case "Inventor"  -> ColorAnsi.BLUE;
            case "Collector" -> ColorAnsi.GREEN;
            default          -> ColorAnsi.RESET;
        };
    }

    /**
     * Builds the seven-line textual box for a card.
     *
     * @param id card identifier, or null for an empty slot
     * @return array containing the rendered box lines
     */
    public static String[] cardBox(Integer id) {
        if (id == null) {
            return new String[]{
                    "┌─────────────┐",
                    "│    free     │",
                    "│             │",
                    "│             │",
                    "│             │",
                    "│             │",
                    "└─────────────┘"
            };
        }

        CardInfo info = CardRegistry.getCard(id);

        // Formattazione UI applicata SOLO QUI, partendo dai numeri puri
        String costString = info.foodCost() > 0 ? "cost: " + info.foodCost() + "f" : "";
        String ppString = info.prestigePoints() > 0 ? "+" + info.prestigePoints() + " pp" : "";
        String costPp = costString.isEmpty() && ppString.isEmpty() ? "" : costString + " " + ppString;

        String eraStr = "Era " + info.era();
        String[] nameParts = splitName(info.name(), 13);

        String detailText = "";
        if (info.type() != null) {
            switch (info.type()) {
                case "Builder" -> detailText = "-" + info.foodDiscount() + "f +" + info.bonusPrestige() + "pp";
                case "Shaman" -> detailText = "★ x" + info.stars();
                case "Hunter" -> detailText = "sym: " + (info.hasIcon() ? "✓" : "✗");
                case "Inventor" -> detailText = info.inventorIcon() != null ? info.inventorIcon() : "";
                case "Collector" -> detailText = "-" + info.sustenanceDiscount() + " food";
                case "Event" -> {
                    String name = info.name();
                    if (name.contains("CavePaintings")) {
                        detailText = "≥" + info.val1() + "a +" + info.val3() + "/a";
                    } else if (name.contains("Hunt")) {
                        detailText = "+" + info.val1() + "f +" + info.val2() + "/h";
                    } else if (name.contains("ShamanicRitual")) {
                        detailText = "+" + info.val1() + "/" + info.val2() + "pp";
                    } else if (name.contains("Sustenance")) {
                        detailText = "-" + info.val1() + "f/char";
                    }
                }
                // Per Artist, Building standard, ecc., detailText rimane ""
            }
        }

        return new String[]{
                "┌─────────────┐",
                "│" + center(nameParts[0], 13) + "│",
                "│" + center(nameParts[1], 13) + "│",
                "│" + center(detailText,     13) + "│",
                "│" + center(costPp.trim(),  13) + "│",
                "│" + center(eraStr,         13) + "│",
                "└─────────────┘"
        };
    }

    /**
     * Prints one or more wrapped rows of rendered card boxes.
     *
     * @param out output port used for printing
     * @param cards cards to render
     * @param showIndices true to print board indices above the cards
     */
    public static void printCardRow(OutputPort out, List<Integer> cards, boolean showIndices) {
        if (cards == null || cards.isEmpty()) {
            out.print("  (no cards)");
            return;
        }

        int chunkSize = 8; // Numero massimo di carte per riga
        for (int start = 0; start < cards.size(); start += chunkSize) {
            int end = Math.min(start + chunkSize, cards.size());
            List<Integer> chunk = cards.subList(start, end);

            if (showIndices) {
                StringBuilder idxBuilder = new StringBuilder("  ");
                for (int i = 0; i < chunk.size(); i++) {
                    int actualIndex = start + i;
                    idxBuilder.append(ColorAnsi.BLACK_BOLD).append(center("[" + actualIndex + "]", 15)).append(ColorAnsi.RESET);
                    if (i < chunk.size() - 1) idxBuilder.append(" ");
                }
                out.print(idxBuilder.toString());
            }

            List<String[]> boxes = chunk.stream().map(CardBoxRenderer::cardBox).toList();
            for (int line = 0; line < 7; line++) {
                StringBuilder sb = new StringBuilder("  ");
                for (int i = 0; i < boxes.size(); i++) {
                    String color = ansiColor(chunk.get(i));
                    sb.append(color).append(boxes.get(i)[line]).append(ColorAnsi.RESET);
                    if (i < boxes.size() - 1) sb.append(" ");
                }
                out.print(sb.toString());
            }
            if (end < cards.size()) out.print("");
        }
    }


    private static String[] splitName(String name, int width) {
        int paren = name.indexOf('(');
        if (paren > 0) return new String[]{name.substring(0, paren), name.substring(paren)};
        if (name.length() <= width) return new String[]{name, ""};
        int at = name.lastIndexOf(' ', width - 1);
        if (at > 0) return new String[]{name.substring(0, at), name.substring(at + 1)};
        return new String[]{name.substring(0, width), name.substring(width)};
    }

    private static String center(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        int pad   = width - s.length();
        int left  = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
}

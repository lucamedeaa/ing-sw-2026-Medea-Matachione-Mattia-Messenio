package it.polimi.ingsw.client.tui.render;

import java.util.function.Consumer;

public final class AnsiColors {
    private AnsiColors() {}

    public static final String RESET = "\033[0m";
    public static final String BOLD = "\033[1m";
    public static final String ITALIC = "\033[3m";
    public static final String CLEAR = "\033[H\033[2J"; // Per pulire lo schermo

    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String MAGENTA = "\033[35m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";
    public static final String GRAY = "\033[90m";

    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";
    public static final String YELLOW_BOLD = "\033[1;33m";
    public static final String BLUE_BOLD = "\033[1;34m";
    public static final String CYAN_BOLD = "\033[1;36m";
    public static final String WHITE_BOLD = "\033[1;37m";
    public static final String BLACK_BOLD = "\033[1;30m";

    public static final String BG_RED_WHITE_TEXT = "\033[1;41;37m";

    // Colori Totem & Logo (256-color mode)
    public static final String ORANGE = "\033[38;5;208m";
    private static final String[] LOGO_GRADIENT = {
            "\033[38;5;226m", "\033[38;5;220m", "\033[38;5;214m",
            "\033[38;5;208m", "\033[38;5;202m", "\033[38;5;166m"
    };

    private static final String[] LOGO_LINES = {
            "███╗   ███╗███████╗███████╗ ██████╗ ███████╗",
            "████╗ ████║██╔════╝██╔════╝██╔═══██╗██╔════╝",
            "██╔████╔██║█████╗  ███████╗██║   ██║███████╗",
            "██║╚██╔╝██║██╔══╝  ╚════██║██║   ██║╚════██║",
            "██║ ╚═╝ ██║███████╗███████║╚██████╔╝███████║",
            "╚═╝     ╚═╝╚══════╝╚══════╝ ╚═════╝ ╚══════╝"
    };

    public static void printLogo(Consumer<String> printer) {
        for (int i = 0; i < LOGO_LINES.length; i++) {
            printer.accept(LOGO_GRADIENT[i] + LOGO_LINES[i] + RESET);
        }
    }
}
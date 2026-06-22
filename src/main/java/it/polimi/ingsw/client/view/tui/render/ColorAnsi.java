package it.polimi.ingsw.client.view.tui.render;

import java.util.function.Consumer;

/**
 * ANSI escape-code constants and logo rendering helpers used by the TUI.
 */
public final class ColorAnsi {
    private ColorAnsi() {}

    /** ANSI sequence that resets all styles and colors. */
    public static final String RESET = "\033[0m";

    /** ANSI sequence that enables bold text. */
    public static final String BOLD = "\033[1m";

    /** ANSI sequence that enables italic text. */
    public static final String ITALIC = "\033[3m";

    /** ANSI sequence that clears the terminal screen. */
    public static final String CLEAR = "\033[H\033[2J"; // Per pulire lo schermo

    /** ANSI foreground color for red text. */
    public static final String RED = "\033[31m";

    /** ANSI foreground color for green text. */
    public static final String GREEN = "\033[32m";

    /** ANSI foreground color for yellow text. */
    public static final String YELLOW = "\033[33m";

    /** ANSI foreground color for blue text. */
    public static final String BLUE = "\033[34m";

    /** ANSI foreground color for magenta text. */
    public static final String MAGENTA = "\033[35m";

    /** ANSI foreground color for cyan text. */
    public static final String CYAN = "\033[36m";


    /** ANSI foreground color for gray text. */
    public static final String GRAY = "\033[90m";

    /** ANSI foreground color for bold red text. */
    public static final String RED_BOLD = "\033[1;31m";

    /** ANSI foreground color for bold green text. */
    public static final String GREEN_BOLD = "\033[1;32m";

    /** ANSI foreground color for bold yellow text. */
    public static final String YELLOW_BOLD = "\033[1;33m";

    /** ANSI foreground color for bold blue text. */
    public static final String BLUE_BOLD = "\033[1;34m";

    /** ANSI foreground color for bold cyan text. */
    public static final String CYAN_BOLD = "\033[1;36m";

    /** ANSI foreground color for bold white text. */
    public static final String WHITE_BOLD = "\033[1;37m";

    /** ANSI foreground color for bold black text. */
    public static final String BLACK_BOLD = "\033[1;30m";

    /** ANSI sequence for red background with white bold text. */
    public static final String BG_RED_WHITE_TEXT = "\033[1;41;37m";

    // Colori Totem & Logo (256-color mode)
    /** ANSI 256-color foreground used for orange totems. */
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

    /**
     * Prints the Mesos logo using a fixed ANSI color gradient.
     *
     * @param printer consumer that receives each rendered logo line
     */
    public static void printLogo(Consumer<String> printer) {
        for (int i = 0; i < LOGO_LINES.length; i++) {
            printer.accept(LOGO_GRADIENT[i] + LOGO_LINES[i] + RESET);
        }
    }
}

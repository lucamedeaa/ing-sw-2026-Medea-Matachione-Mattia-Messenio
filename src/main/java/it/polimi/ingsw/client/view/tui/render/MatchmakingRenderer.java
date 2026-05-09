package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.common.network.dto.GameInfoDto;

import java.util.List;

public class MatchmakingRenderer {
    private final OutputPort out;

    public MatchmakingRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(List<GameInfoDto> availableGames, String lastError) {
        out.clearScreen();
        ColorAnsi.printLogo(out::print);
        printHeader();
        printCommands();
        if (availableGames != null) {
            printGames(availableGames);
        }

        if (lastError != null && !lastError.isEmpty()) {
            out.print(ColorAnsi.YELLOW + "\n[INFO]" + ColorAnsi.RESET + " " + ColorAnsi.ITALIC + lastError + ColorAnsi.RESET);
        }
        out.prompt(ColorAnsi.YELLOW_BOLD +"\nType in your destination > " + ColorAnsi.RESET);
    }


    private void printHeader() {
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);
        out.print(ColorAnsi.WHITE_BOLD + "Welcome, Chief. Make your mark on history." + ColorAnsi.RESET);
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);
    }

    private void printCommands() {
        out.print("   " + ColorAnsi.YELLOW_BOLD + "•" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "list" + ColorAnsi.RESET + "       " + ColorAnsi.GRAY + "| See the Fixtures (Upcoming matches)" + ColorAnsi.RESET);
        out.print("   " + ColorAnsi.YELLOW_BOLD + "•" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "create <nickname> <players>" + ColorAnsi.RESET + "     " + ColorAnsi.GRAY + "| Found a new settlement" + ColorAnsi.RESET);
        out.print("   " + ColorAnsi.YELLOW_BOLD + "•" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "join <nickname> <gameID>" + ColorAnsi.RESET + "       " + ColorAnsi.GRAY + "| Join an existing tribe" + ColorAnsi.RESET);
        out.print("   " + ColorAnsi.YELLOW_BOLD + "•" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "0" + ColorAnsi.RESET + "          " + ColorAnsi.GRAY + "| Leave History and exit" + ColorAnsi.RESET + "\n");
    }

    private void printGames(List<GameInfoDto> availableGames) {
        if (!availableGames.isEmpty()) {
            out.print("   " + ColorAnsi.GREEN_BOLD + "ACTIVE CHRONICLES:" + ColorAnsi.RESET);
            for (GameInfoDto g : availableGames) {
                out.print(String.format("   " + ColorAnsi.YELLOW + "▶" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "ID: %-8s" + ColorAnsi.RESET + " " + ColorAnsi.GRAY + "| Founder:" + ColorAnsi.RESET + " %-12s " + ColorAnsi.GRAY + "| Population:" + ColorAnsi.RESET + " [%d/%d]",
                        g.getGameId(), g.getCreatorNickname(), g.getCurrentPlayers(), g.getMaxPlayers()));
            }
        } else {
            out.print("   " + ColorAnsi.ITALIC + "No stories yet. Be the first to leave your mark." + ColorAnsi.RESET);
        }
    }
}
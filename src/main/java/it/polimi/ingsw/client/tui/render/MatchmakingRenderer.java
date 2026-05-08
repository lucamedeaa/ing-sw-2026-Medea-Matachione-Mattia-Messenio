package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.network.messages.GameInfoDTO;

import java.util.List;

public class MatchmakingRenderer {
    private final OutputPort out;

    public MatchmakingRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(List<GameInfoDTO> availableGames, String lastError) {
        out.clearScreen();
        AnsiColors.printLogo(out::print);
        printHeader();
        printCommands();
        if (availableGames != null) {
            printGames(availableGames);
        }

        if (lastError != null && !lastError.isEmpty()) {
            out.print(AnsiColors.YELLOW + "\n[INFO]" + AnsiColors.RESET + " " + AnsiColors.ITALIC + lastError + AnsiColors.RESET);
        }
        out.prompt(AnsiColors.YELLOW_BOLD +"\nDigita il tuo destino > " + AnsiColors.RESET);
    }


    private void printHeader() {
        out.print(AnsiColors.BLACK_BOLD + "━".repeat(52) + AnsiColors.RESET);
        out.print(AnsiColors.WHITE_BOLD + "Benvenuto Capotribù. Incidi la tua storia nel tempo." + AnsiColors.RESET);
        out.print(AnsiColors.BLACK_BOLD + "━".repeat(52) + AnsiColors.RESET);
    }

    private void printCommands() {
        out.print("   " + AnsiColors.YELLOW_BOLD + "•" + AnsiColors.RESET + " " + AnsiColors.BOLD + "list" + AnsiColors.RESET + "       " + AnsiColors.GRAY + "| Osserva le Cronache (Partite disponibili)" + AnsiColors.RESET);
        out.print("   " + AnsiColors.YELLOW_BOLD + "•" + AnsiColors.RESET + " " + AnsiColors.BOLD + "create <nickname> <players>" + AnsiColors.RESET + "     " + AnsiColors.GRAY + "| Fonda un nuovo Insediamento" + AnsiColors.RESET);
        out.print("   " + AnsiColors.YELLOW_BOLD + "•" + AnsiColors.RESET + " " + AnsiColors.BOLD + "join <nickname> <gameID>" + AnsiColors.RESET + "       " + AnsiColors.GRAY + "| Unisciti a una Tribù esistente" + AnsiColors.RESET);
        out.print("   " + AnsiColors.YELLOW_BOLD + "•" + AnsiColors.RESET + " " + AnsiColors.BOLD + "0" + AnsiColors.RESET + "          " + AnsiColors.GRAY + "| Abbandona la Storia ed esci" + AnsiColors.RESET + "\n");
    }

    private void printGames(List<GameInfoDTO> availableGames) {
        if (!availableGames.isEmpty()) {
            out.print("   " + AnsiColors.GREEN_BOLD + "CRONACHE ATTIVE:" + AnsiColors.RESET);
            for (GameInfoDTO g : availableGames) {
                out.print(String.format("   " + AnsiColors.YELLOW + "▶" + AnsiColors.RESET + " " + AnsiColors.BOLD + "ID: %-8s" + AnsiColors.RESET + " " + AnsiColors.GRAY + "| Fondatore:" + AnsiColors.RESET + " %-12s " + AnsiColors.GRAY + "| Popolazione:" + AnsiColors.RESET + " [%d/%d]",
                        g.getGameId(), g.getCreatorNickname(), g.getCurrentPlayers(), g.getMaxPlayers()));
            }
        } else {
            out.print("   " + AnsiColors.ITALIC + "Nessuna storia iniziata. Sii il primo a incidere la pietra." + AnsiColors.RESET);
        }
    }
}
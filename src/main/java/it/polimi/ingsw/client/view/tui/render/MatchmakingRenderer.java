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
        out.prompt(ColorAnsi.YELLOW_BOLD +"\nDigita il tuo destino > " + ColorAnsi.RESET);
    }


    private void printHeader() {
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);
        out.print(ColorAnsi.WHITE_BOLD + "Benvenuto Capotribù. Incidi la tua storia nel tempo." + ColorAnsi.RESET);
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);
    }

    private void printCommands() {
        out.print("   " + ColorAnsi.YELLOW_BOLD + "•" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "list" + ColorAnsi.RESET + "       " + ColorAnsi.GRAY + "| Osserva le Cronache (Partite disponibili)" + ColorAnsi.RESET);
        out.print("   " + ColorAnsi.YELLOW_BOLD + "•" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "create <nickname> <players>" + ColorAnsi.RESET + "     " + ColorAnsi.GRAY + "| Fonda un nuovo Insediamento" + ColorAnsi.RESET);
        out.print("   " + ColorAnsi.YELLOW_BOLD + "•" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "join <nickname> <gameID>" + ColorAnsi.RESET + "       " + ColorAnsi.GRAY + "| Unisciti a una Tribù esistente" + ColorAnsi.RESET);
        out.print("   " + ColorAnsi.YELLOW_BOLD + "•" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "0" + ColorAnsi.RESET + "          " + ColorAnsi.GRAY + "| Abbandona la Storia ed esci" + ColorAnsi.RESET + "\n");
    }

    private void printGames(List<GameInfoDto> availableGames) {
        if (!availableGames.isEmpty()) {
            out.print("   " + ColorAnsi.GREEN_BOLD + "CRONACHE ATTIVE:" + ColorAnsi.RESET);
            for (GameInfoDto g : availableGames) {
                out.print(String.format("   " + ColorAnsi.YELLOW + "▶" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + "ID: %-8s" + ColorAnsi.RESET + " " + ColorAnsi.GRAY + "| Fondatore:" + ColorAnsi.RESET + " %-12s " + ColorAnsi.GRAY + "| Popolazione:" + ColorAnsi.RESET + " [%d/%d]",
                        g.getGameId(), g.getCreatorNickname(), g.getCurrentPlayers(), g.getMaxPlayers()));
            }
        } else {
            out.print("   " + ColorAnsi.ITALIC + "Nessuna storia iniziata. Sii il primo a incidere la pietra." + ColorAnsi.RESET);
        }
    }
}
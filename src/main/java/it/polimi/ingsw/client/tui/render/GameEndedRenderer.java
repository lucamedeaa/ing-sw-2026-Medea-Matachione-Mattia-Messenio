package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.network.dto.LeaderboardSnapshot;
import it.polimi.ingsw.network.dto.PlayerGameCompletedDTO;
import it.polimi.ingsw.network.dto.PlayerScoreDTO;

import java.util.List;

public class GameEndedRenderer {
    private final OutputPort out;

    public GameEndedRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(PlayerGameCompletedDTO local, LeaderboardSnapshot global) {
        out.clearScreen();
        out.print(AnsiColors.YELLOW_BOLD + "█".repeat(60) + AnsiColors.RESET);
        out.print(AnsiColors.WHITE_BOLD + "                 LE CRONACHE DI MESOS SONO SCRITTE               " + AnsiColors.RESET);
        out.print(AnsiColors.BLACK_BOLD + "━".repeat(60) + AnsiColors.RESET + "\n");

        out.print("  " + AnsiColors.CYAN_BOLD + "I TUOI RISULTATI NELLA TRIBU':" + AnsiColors.RESET);
        out.print("    " + AnsiColors.GRAY + "Posizione Finale:" + AnsiColors.RESET + "  " + AnsiColors.YELLOW_BOLD + local.localPosition() + "°" + AnsiColors.RESET + " su " + local.playerCount() + " capitribù");
        out.print("    " + AnsiColors.GRAY + "Prestigio:" + AnsiColors.RESET + "         " + AnsiColors.GREEN_BOLD + local.localScore() + " PP" + AnsiColors.RESET);
        out.print("    " + AnsiColors.GRAY + "Cibo Rimanente:" + AnsiColors.RESET + "    " + local.localRemainingFood() + "\n");

        out.print("  " + AnsiColors.BOLD + AnsiColors.MAGENTA + "LA TUA LEGGENDA PERSONALE (Miglior Punteggio Storico):" + AnsiColors.RESET);
        out.print("    " + AnsiColors.GRAY + "Posizione Globale:" + AnsiColors.RESET + " " + AnsiColors.BOLD + local.globalPersonalBestPosition() + "°" + AnsiColors.RESET);
        out.print("    " + AnsiColors.GRAY + "Punteggio Massimo:" + AnsiColors.RESET + " " + AnsiColors.GREEN_BOLD + local.personalBestScore() + " PP" + AnsiColors.RESET + " (Cibo: " + local.personalBestRemainingFood() + ")\n");

        out.print(AnsiColors.BLACK_BOLD + "━".repeat(60) + AnsiColors.RESET);

        if (global != null) {
            out.print("  " + AnsiColors.WHITE_BOLD + "🏆 CLASSIFICA GLOBALE DELLA VALLE (" + global.playerCount() + " giocatori) 🏆" + AnsiColors.RESET + "\n");
            for (var entry : global.entries()) {
                String highlight = entry.nickname().equals(navNicknamePlaceholder(entry.nickname())) ? AnsiColors.GREEN_BOLD : AnsiColors.WHITE_BOLD;
                out.print(String.format("   %s%2d." + AnsiColors.RESET + " %-18s " + AnsiColors.GRAY + "|" + AnsiColors.RESET + " " + AnsiColors.YELLOW_BOLD + "%3d PP" + AnsiColors.RESET + " " + AnsiColors.GRAY + "|" + AnsiColors.RESET + " Cibo: %-2d " + AnsiColors.GRAY + "|" + AnsiColors.RESET + " Data: %s",
                        highlight, entry.position(), entry.nickname(), entry.finalScore(), entry.remainingFood(), entry.playedAt().toString().substring(0, 10)));
            }
        } else {
            out.print("  " + AnsiColors.ITALIC + "Recupero della classifica globale dalle pietre antiche..." + AnsiColors.RESET);
        }

        out.print("\n" + AnsiColors.BLACK_BOLD + "━".repeat(60) + AnsiColors.RESET);
        out.print(" " + AnsiColors.YELLOW_BOLD + "[ 0 ]" + AnsiColors.RESET + " " + AnsiColors.WHITE_BOLD + "Torna al Menu" + AnsiColors.RESET + "   " + AnsiColors.RED_BOLD + "[ d ]" + AnsiColors.RESET + " " + AnsiColors.WHITE_BOLD + "Disconnetti" + AnsiColors.RESET);
        out.prompt("\n" + AnsiColors.GREEN_BOLD + "> " + AnsiColors.RESET);
    }

    private String navNicknamePlaceholder(String entryNickname) {
        return "";
    }
}
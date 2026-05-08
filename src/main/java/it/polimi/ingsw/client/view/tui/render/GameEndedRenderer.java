package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;

public class GameEndedRenderer {
    private final OutputPort out;

    public GameEndedRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(PlayerGameCompletedDto local, LeaderboardSnapshotDto global) {
        out.clearScreen();
        out.print(ColorAnsi.YELLOW_BOLD + "█".repeat(60) + ColorAnsi.RESET);
        out.print(ColorAnsi.WHITE_BOLD + "                 LE CRONACHE DI MESOS SONO SCRITTE               " + ColorAnsi.RESET);
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(60) + ColorAnsi.RESET + "\n");

        out.print("  " + ColorAnsi.CYAN_BOLD + "I TUOI RISULTATI NELLA TRIBU':" + ColorAnsi.RESET);
        out.print("    " + ColorAnsi.GRAY + "Posizione Finale:" + ColorAnsi.RESET + "  " + ColorAnsi.YELLOW_BOLD + local.localPosition() + "°" + ColorAnsi.RESET + " su " + local.playerCount() + " capitribù");
        out.print("    " + ColorAnsi.GRAY + "Prestigio:" + ColorAnsi.RESET + "         " + ColorAnsi.GREEN_BOLD + local.localScore() + " PP" + ColorAnsi.RESET);
        out.print("    " + ColorAnsi.GRAY + "Cibo Rimanente:" + ColorAnsi.RESET + "    " + local.localRemainingFood() + "\n");

        out.print("  " + ColorAnsi.BOLD + ColorAnsi.MAGENTA + "LA TUA LEGGENDA PERSONALE (Miglior Punteggio Storico):" + ColorAnsi.RESET);
        out.print("    " + ColorAnsi.GRAY + "Posizione Globale:" + ColorAnsi.RESET + " " + ColorAnsi.BOLD + local.globalPersonalBestPosition() + "°" + ColorAnsi.RESET);
        out.print("    " + ColorAnsi.GRAY + "Punteggio Massimo:" + ColorAnsi.RESET + " " + ColorAnsi.GREEN_BOLD + local.personalBestScore() + " PP" + ColorAnsi.RESET + " (Cibo: " + local.personalBestRemainingFood() + ")\n");

        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(60) + ColorAnsi.RESET);

        if (global != null) {
            out.print("  " + ColorAnsi.WHITE_BOLD + "🏆 CLASSIFICA GLOBALE DELLA VALLE (" + global.playerCount() + " giocatori) 🏆" + ColorAnsi.RESET + "\n");
            for (var entry : global.entries()) {
                String highlight = entry.nickname().equals(navNicknamePlaceholder(entry.nickname())) ? ColorAnsi.GREEN_BOLD : ColorAnsi.WHITE_BOLD;
                out.print(String.format("   %s%2d." + ColorAnsi.RESET + " %-18s " + ColorAnsi.GRAY + "|" + ColorAnsi.RESET + " " + ColorAnsi.YELLOW_BOLD + "%3d PP" + ColorAnsi.RESET + " " + ColorAnsi.GRAY + "|" + ColorAnsi.RESET + " Cibo: %-2d " + ColorAnsi.GRAY + "|" + ColorAnsi.RESET + " Data: %s",
                        highlight, entry.position(), entry.nickname(), entry.finalScore(), entry.remainingFood(), entry.playedAt().toString().substring(0, 10)));
            }
        } else {
            out.print("  " + ColorAnsi.ITALIC + "Recupero della classifica globale dalle pietre antiche..." + ColorAnsi.RESET);
        }

        out.print("\n" + ColorAnsi.BLACK_BOLD + "━".repeat(60) + ColorAnsi.RESET);
        out.print(" " + ColorAnsi.YELLOW_BOLD + "[ 0 ]" + ColorAnsi.RESET + " " + ColorAnsi.WHITE_BOLD + "Torna al Menu" + ColorAnsi.RESET + "   " + ColorAnsi.RED_BOLD + "[ d ]" + ColorAnsi.RESET + " " + ColorAnsi.WHITE_BOLD + "Disconnetti" + ColorAnsi.RESET);
        out.prompt("\n" + ColorAnsi.GREEN_BOLD + "> " + ColorAnsi.RESET);
    }

    private String navNicknamePlaceholder(String entryNickname) {
        return "";
    }
}
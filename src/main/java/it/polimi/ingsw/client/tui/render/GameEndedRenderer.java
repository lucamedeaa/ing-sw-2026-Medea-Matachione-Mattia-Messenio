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
        out.print("\033[1;33m" + "█".repeat(60) + "\033[0m");
        out.print("\033[1;37m                 LE CRONACHE DI MESOS SONO SCRITTE               \033[0m");
        out.print("\033[1;30m" + "━".repeat(60) + "\033[0m\n");

        // Risultato Locale
        out.print("  \033[1;36mI TUOI RISULTATI NELLA TRIBU':\033[0m");
        out.print("    \033[90mPosizione Finale:\033[0m  \033[1;33m" + local.localPosition() + "°\033[0m su " + local.playerCount() + " capitribù");
        out.print("    \033[90mPrestigio:\033[0m         \033[1;32m" + local.localScore() + " PP\033[0m");
        out.print("    \033[90mCibo Rimanente:\033[0m    " + local.localRemainingFood() + "\n");

        out.print("  \033[1;35mLA TUA LEGGENDA PERSONALE (Miglior Punteggio Storico):\033[0m");
        out.print("    \033[90mPosizione Globale:\033[0m \033[1m" + local.globalPersonalBestPosition() + "°\033[0m");
        out.print("    \033[90mPunteggio Massimo:\033[0m \033[1;32m" + local.personalBestScore() + " PP\033[0m (Cibo: " + local.personalBestRemainingFood() + ")\n");

        out.print("\033[1;30m" + "━".repeat(60) + "\033[0m");

        // Risultato Globale
        if (global != null) {
            out.print("  \033[1;37m🏆 CLASSIFICA GLOBALE DELLA VALLE (" + global.playerCount() + " giocatori) 🏆\033[0m\n");
            for (var entry : global.entries()) {
                String highlight = entry.nickname().equals(navNicknamePlaceholder(entry.nickname())) ? "\033[1;32m" : "\033[1;37m";
                out.print(String.format("   %s%2d.\033[0m %-18s \033[90m|\033[0m \033[1;33m%3d PP\033[0m \033[90m|\033[0m Cibo: %-2d \033[90m|\033[0m Data: %s",
                        highlight, entry.position(), entry.nickname(), entry.finalScore(), entry.remainingFood(), entry.playedAt().toString().substring(0, 10)));
            }
        } else {
            out.print("  \033[3mRecupero della classifica globale dalle pietre antiche...\033[0m");
        }

        out.print("\n\033[1;30m" + "━".repeat(60) + "\033[0m");
        out.print(" \033[1;33m[ 0 ]\033[0m \033[1;37mTorna al Menu\033[0m   \033[1;31m[ d ]\033[0m \033[1;37mDisconnetti\033[0m");
        out.prompt("\n\033[1;32m> \033[0m");
    }

    private String navNicknamePlaceholder(String entryNickname) {
        return "";
    }
}
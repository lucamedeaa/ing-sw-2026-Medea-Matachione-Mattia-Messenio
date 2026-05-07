package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.OutputPort;
import java.util.List;

public class LobbyRenderer {
    private final OutputPort out;

    public LobbyRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(List<String> currentPlayers, String notification, String myNickname) {
        out.clearScreen();
        out.print("\033[1;33m" + "█".repeat(52) + "\033[0m");
        out.print("\033[1;37m   MESOS   \033[0m| \033[1;32mLOBBY DELL'INSEDIAMENTO\033[0m");
        out.print("\033[1;30m" + "━".repeat(52) + "\033[0m\n");

        out.print("\033[1;37mEsploratori pronti al viaggio:\033[0m");
        if (currentPlayers.isEmpty()) {
            out.print(" \033[90m  Nessun membro trovato nell'accampamento...\033[0m");
        } else {
            for (String p : currentPlayers) {
                boolean isMe = p.equals(myNickname);
                String color = isMe ? "\033[1;32m" : "\033[1;37m";
                String marker = isMe ? " \033[1;32m(tu)\033[0m" : "";
                out.print(" \033[33m▶\033[0m " + color + String.format("%-15s", p) + marker + "\033[0m");
            }
        }

        if (notification != null && !notification.isEmpty()) {
            out.print("\n\033[1;34mℹ ECO DALLA VALLE:\033[0m \033[3m" + notification + "\033[0m");
        }

        out.print("\n\033[1;30m" + "━".repeat(52) + "\033[0m");
        out.print(" \033[1;33m[ 0 ]\033[0m \033[1;37mAbbandona\033[0m \033[90m| Torna alla ricerca di altre storie\033[0m");
        out.print(" \033[1;31m[ d ]\033[0m \033[1;37mSvanisci\033[0m  \033[90m| Disconnettiti dal mondo di Mesos\033[0m");
        out.print("\033[1;30m" + "━".repeat(52) + "\033[0m");

        out.prompt("\n\033[1;33mIn attesa che la tribù sia al completo > \033[0m");
    }
}
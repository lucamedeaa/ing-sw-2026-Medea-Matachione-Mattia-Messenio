package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import java.util.List;

public class LobbyRenderer {
    private final OutputPort out;

    public LobbyRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(List<String> currentPlayers, String notification, String myNickname) {
        out.clearScreen();
        out.print(ColorAnsi.YELLOW_BOLD + "█".repeat(52) + ColorAnsi.RESET);
        out.print(ColorAnsi.WHITE_BOLD + "   MESOS   " + ColorAnsi.RESET + "| " + ColorAnsi.GREEN_BOLD + "LOBBY DELL'INSEDIAMENTO" + ColorAnsi.RESET);
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET + "\n");

        out.print(ColorAnsi.WHITE_BOLD + "Esploratori pronti al viaggio:" + ColorAnsi.RESET);
        if (currentPlayers.isEmpty()) {
            out.print(" " + ColorAnsi.GRAY + "  Nessun membro trovato nell'accampamento..." + ColorAnsi.RESET);
        } else {
            for (String p : currentPlayers) {
                boolean isMe = p.equals(myNickname);
                String color = isMe ? ColorAnsi.GREEN_BOLD : ColorAnsi.WHITE_BOLD;
                String marker = isMe ? " " + ColorAnsi.GREEN_BOLD + "(tu)" + ColorAnsi.RESET : "";
                out.print(" " + ColorAnsi.YELLOW + "▶" + ColorAnsi.RESET + " " + color + String.format("%-15s", p) + marker + ColorAnsi.RESET);
            }
        }

        if (notification != null && !notification.isEmpty()) {
            out.print("\n" + ColorAnsi.BLUE_BOLD + "ℹ ECO DALLA VALLE:" + ColorAnsi.RESET + " " + ColorAnsi.ITALIC + notification + ColorAnsi.RESET);
        }

        out.print("\n" + ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);
        out.print(" " + ColorAnsi.YELLOW_BOLD + "[ 0 ]" + ColorAnsi.RESET + " " + ColorAnsi.WHITE_BOLD + "Abbandona" + ColorAnsi.RESET + " " + ColorAnsi.GRAY + "| Torna alla ricerca di altre storie" + ColorAnsi.RESET);
        out.print(" " + ColorAnsi.RED_BOLD + "[ d ]" + ColorAnsi.RESET + " " + ColorAnsi.WHITE_BOLD + "Svanisci" + ColorAnsi.RESET + "  " + ColorAnsi.GRAY + "| Disconnettiti dal mondo di Mesos" + ColorAnsi.RESET);
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);

        out.prompt("\n" + ColorAnsi.YELLOW_BOLD + "In attesa che la tribù sia al completo > " + ColorAnsi.RESET);
    }
}
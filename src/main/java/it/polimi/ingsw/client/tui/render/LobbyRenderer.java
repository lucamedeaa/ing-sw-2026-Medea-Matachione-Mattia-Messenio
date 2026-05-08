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
        out.print(AnsiColors.YELLOW_BOLD + "█".repeat(52) + AnsiColors.RESET);
        out.print(AnsiColors.WHITE_BOLD + "   MESOS   " + AnsiColors.RESET + "| " + AnsiColors.GREEN_BOLD + "LOBBY DELL'INSEDIAMENTO" + AnsiColors.RESET);
        out.print(AnsiColors.BLACK_BOLD + "━".repeat(52) + AnsiColors.RESET + "\n");

        out.print(AnsiColors.WHITE_BOLD + "Esploratori pronti al viaggio:" + AnsiColors.RESET);
        if (currentPlayers.isEmpty()) {
            out.print(" " + AnsiColors.GRAY + "  Nessun membro trovato nell'accampamento..." + AnsiColors.RESET);
        } else {
            for (String p : currentPlayers) {
                boolean isMe = p.equals(myNickname);
                String color = isMe ? AnsiColors.GREEN_BOLD : AnsiColors.WHITE_BOLD;
                String marker = isMe ? " " + AnsiColors.GREEN_BOLD + "(tu)" + AnsiColors.RESET : "";
                out.print(" " + AnsiColors.YELLOW + "▶" + AnsiColors.RESET + " " + color + String.format("%-15s", p) + marker + AnsiColors.RESET);
            }
        }

        if (notification != null && !notification.isEmpty()) {
            out.print("\n" + AnsiColors.BLUE_BOLD + "ℹ ECO DALLA VALLE:" + AnsiColors.RESET + " " + AnsiColors.ITALIC + notification + AnsiColors.RESET);
        }

        out.print("\n" + AnsiColors.BLACK_BOLD + "━".repeat(52) + AnsiColors.RESET);
        out.print(" " + AnsiColors.YELLOW_BOLD + "[ 0 ]" + AnsiColors.RESET + " " + AnsiColors.WHITE_BOLD + "Abbandona" + AnsiColors.RESET + " " + AnsiColors.GRAY + "| Torna alla ricerca di altre storie" + AnsiColors.RESET);
        out.print(" " + AnsiColors.RED_BOLD + "[ d ]" + AnsiColors.RESET + " " + AnsiColors.WHITE_BOLD + "Svanisci" + AnsiColors.RESET + "  " + AnsiColors.GRAY + "| Disconnettiti dal mondo di Mesos" + AnsiColors.RESET);
        out.print(AnsiColors.BLACK_BOLD + "━".repeat(52) + AnsiColors.RESET);

        out.prompt("\n" + AnsiColors.YELLOW_BOLD + "In attesa che la tribù sia al completo > " + AnsiColors.RESET);
    }
}
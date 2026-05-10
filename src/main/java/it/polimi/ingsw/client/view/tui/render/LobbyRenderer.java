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
        out.print(ColorAnsi.WHITE_BOLD + "   MESOS   " + ColorAnsi.RESET + "| " + ColorAnsi.GREEN_BOLD + "SETTLEMENT LOBBY" + ColorAnsi.RESET);
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET + "\n");

        out.print(ColorAnsi.WHITE_BOLD + "Explorers ready for the journey:" + ColorAnsi.RESET);
        if (currentPlayers.isEmpty()) {
            out.print(" " + ColorAnsi.GRAY + "  No members found in the camp..." + ColorAnsi.RESET);
        } else {
            for (String p : currentPlayers) {
                boolean isMe = p.equals(myNickname);
                String color = isMe ? ColorAnsi.GREEN_BOLD : ColorAnsi.WHITE_BOLD;
                String marker = isMe ? " " + ColorAnsi.GREEN_BOLD + "(tu)" + ColorAnsi.RESET : "";
                out.print(" " + ColorAnsi.YELLOW + "▶" + ColorAnsi.RESET + " " + color + String.format("%-15s", p) + marker + ColorAnsi.RESET);
            }
        }

        if (notification != null && !notification.isEmpty()) {
            out.print("\n" + ColorAnsi.BLUE_BOLD + "ℹ ECHOES FROM THE VALLEY:" + ColorAnsi.RESET + " " + ColorAnsi.ITALIC + notification + ColorAnsi.RESET);
        }

        out.print("\n" + ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);
        out.print(" " + ColorAnsi.YELLOW_BOLD + "[ 0 ]" + ColorAnsi.RESET + " " + ColorAnsi.WHITE_BOLD + "Give up" + ColorAnsi.RESET + " " + ColorAnsi.GRAY + "| Back to the search for more stories" + ColorAnsi.RESET);
        out.print(" " + ColorAnsi.RED_BOLD + "[ d ]" + ColorAnsi.RESET + " " + ColorAnsi.WHITE_BOLD + "Fade away" + ColorAnsi.RESET + "  " + ColorAnsi.GRAY + "| Disconnect from the world of Mesos" + ColorAnsi.RESET);
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(52) + ColorAnsi.RESET);

        out.prompt("\n" + ColorAnsi.YELLOW_BOLD + "Until the whole tribe is here > " + ColorAnsi.RESET);
    }
}
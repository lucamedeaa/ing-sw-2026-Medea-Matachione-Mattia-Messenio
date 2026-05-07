package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.network.dto.PlayerScoreDTO;

import java.util.List;

public class GameEndedRenderer {
    private final OutputPort out;

    public GameEndedRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(List<String> winners, List<PlayerScoreDTO> leaderboard, String myNickname) {
        out.clearScreen();
        out.print("  ===================================");
        out.print("           VINCITORE/I               ");
        out.print("   🏆 " + String.join(", ", winners) + " 🏆");
        out.print("  ===================================\n");

        out.print("  CLASSIFICA COMPLETA:");
        for (int i = 0; i < leaderboard.size(); i++) {
            boolean isMe = leaderboard.get(i).nickname().equals(myNickname);
            String color = isMe ? "\033[1;32m" : "\033[1;37m";
            out.print(String.format("  %s%d. %-14s %d PP\033[0m",
                    color, i + 1, leaderboard.get(i).nickname(), leaderboard.get(i).finalScore()));
        }
        out.print("\n  0. Torna al menu");
        out.print("  d. Disconnetti");
        out.prompt("\n> ");
    }
}
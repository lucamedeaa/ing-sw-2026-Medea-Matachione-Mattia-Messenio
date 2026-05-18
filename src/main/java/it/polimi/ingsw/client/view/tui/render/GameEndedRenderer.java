package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.common.network.dto.PlayerGameCompletedDto;
import it.polimi.ingsw.common.network.dto.PlayerScoreDto;

import java.util.List;

public class GameEndedRenderer {
    private final OutputPort out;

    public GameEndedRenderer(OutputPort out) {
        this.out = out;
    }

    // IL METODO ORA ACCETTA 5 PARAMETRI
    public void render(
            PlayerGameCompletedDto local,
            List<PlayerScoreDto> sessionScores,
            LeaderboardSnapshotDto leaderboard,
            String myNickname,
            boolean showLeaderboard
    ) {
        out.clearScreen();

        out.print(ColorAnsi.YELLOW_BOLD + "█".repeat(60) + ColorAnsi.RESET);
        out.print(ColorAnsi.WHITE_BOLD + "                 THE CHRONICLES OF MESOS ARE WRITTEN               " + ColorAnsi.RESET);
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(60) + ColorAnsi.RESET + "\n");

        renderSessionScores(sessionScores, myNickname);
        renderLocalResult(local);
        renderPersonalBest(local);

        if (showLeaderboard) {
            renderLeaderboard(leaderboard, myNickname);
        }

        renderFooter(showLeaderboard);
    }

    private void renderSessionScores(List<PlayerScoreDto> sessionScores, String myNickname) {
        out.print("  " + ColorAnsi.CYAN_BOLD + "MATCH RESULTS:" + ColorAnsi.RESET);

        int position = 1;
        for (PlayerScoreDto score : sessionScores) {
            String highlight = score.nickname().equals(myNickname) ? ColorAnsi.GREEN_BOLD : ColorAnsi.WHITE_BOLD;

            out.print("    " + highlight + position + "° - " + score.nickname() + ColorAnsi.RESET
                    + "  " + ColorAnsi.YELLOW_BOLD + score.finalScore() + " PP" + ColorAnsi.RESET
                    + "  (Food: " + score.remainingFood() + ")");
            position++;
        }
        out.print("");
    }

    private void renderLocalResult(PlayerGameCompletedDto local) {
        out.print("  " + ColorAnsi.CYAN_BOLD + "YOUR RESULTS IN THE TRIBE:" + ColorAnsi.RESET);
        out.print("    " + ColorAnsi.GRAY + "Final position:" + ColorAnsi.RESET
                + "  " + ColorAnsi.YELLOW_BOLD + local.localPosition() + "°" + ColorAnsi.RESET
                + " su " + local.playerCount() + " tribal chief");
        out.print("    " + ColorAnsi.GRAY + "Prestige:" + ColorAnsi.RESET
                + "         " + ColorAnsi.GREEN_BOLD + local.localScore() + " PP" + ColorAnsi.RESET);
        out.print("    " + ColorAnsi.GRAY + "Remaining food:" + ColorAnsi.RESET
                + "    " + local.localRemainingFood() + "\n");
    }

    private void renderPersonalBest(PlayerGameCompletedDto local) {
        out.print("  " + ColorAnsi.BOLD + ColorAnsi.MAGENTA
                + "YOUR PERSONAL RECORD (Highest Score Ever):" + ColorAnsi.RESET);
        out.print("    " + ColorAnsi.GRAY + "Overall ranking:" + ColorAnsi.RESET
                + " " + ColorAnsi.BOLD + local.globalPersonalBestPosition() + "°" + ColorAnsi.RESET);
        out.print("    " + ColorAnsi.GRAY + "Maximum score:" + ColorAnsi.RESET
                + " " + ColorAnsi.GREEN_BOLD + local.personalBestScore() + " PP" + ColorAnsi.RESET
                + " (Food: " + local.personalBestRemainingFood() + ")\n");
    }

    private void renderLeaderboard(LeaderboardSnapshotDto leaderboard, String myNickname) {
        out.print(ColorAnsi.BLACK_BOLD + "━".repeat(60) + ColorAnsi.RESET);

        if (leaderboard == null) {
            out.print("  " + ColorAnsi.ITALIC
                    + "Loading the complete leaderboard..." + ColorAnsi.RESET);
            return;
        }

        out.print("  " + ColorAnsi.WHITE_BOLD
                + "FULL LEADERBOARD (" + leaderboard.playerCount() + " players)"
                + ColorAnsi.RESET + "\n");

        if (leaderboard.entries().isEmpty()) {
            out.print("  " + ColorAnsi.ITALIC
                    + "No completed games found for this player count."
                    + ColorAnsi.RESET);
            return;
        }

        for (var entry : leaderboard.entries()) {
            String highlight = entry.nickname().equals(myNickname)
                    ? ColorAnsi.GREEN_BOLD
                    : ColorAnsi.WHITE_BOLD;

            out.print(String.format(
                    "   %s%2d." + ColorAnsi.RESET
                            + " %-18s "
                            + ColorAnsi.GRAY + "|" + ColorAnsi.RESET
                            + " " + ColorAnsi.YELLOW_BOLD + "%3d PP" + ColorAnsi.RESET
                            + " " + ColorAnsi.GRAY + "|" + ColorAnsi.RESET
                            + " Food: %-2d "
                            + ColorAnsi.GRAY + "|" + ColorAnsi.RESET
                            + " Date: %s",
                    highlight,
                    entry.position(),
                    entry.nickname(),
                    entry.finalScore(),
                    entry.remainingFood(),
                    entry.playedAt().toString().substring(0, 10)
            ));
        }
    }

    private void renderFooter(boolean showLeaderboard) {
        out.print("\n" + ColorAnsi.BLACK_BOLD + "━".repeat(60) + ColorAnsi.RESET);

        if (showLeaderboard) {
            out.print(" " + ColorAnsi.YELLOW_BOLD + "[ l ]" + ColorAnsi.RESET
                    + " " + ColorAnsi.WHITE_BOLD + "Refresh leaderboard" + ColorAnsi.RESET
                    + "   " + ColorAnsi.YELLOW_BOLD + "[ 0 ]" + ColorAnsi.RESET
                    + " " + ColorAnsi.WHITE_BOLD + "Back to the menu" + ColorAnsi.RESET
                    + "   " + ColorAnsi.RED_BOLD + "[ d ]" + ColorAnsi.RESET
                    + " " + ColorAnsi.WHITE_BOLD + "Log out" + ColorAnsi.RESET);
        } else {
            out.print(" " + ColorAnsi.YELLOW_BOLD + "[ l ]" + ColorAnsi.RESET
                    + " " + ColorAnsi.WHITE_BOLD + "Full leaderboard" + ColorAnsi.RESET
                    + "   " + ColorAnsi.YELLOW_BOLD + "[ 0 ]" + ColorAnsi.RESET
                    + " " + ColorAnsi.WHITE_BOLD + "Back to the menu" + ColorAnsi.RESET
                    + "   " + ColorAnsi.RED_BOLD + "[ d ]" + ColorAnsi.RESET
                    + " " + ColorAnsi.WHITE_BOLD + "Log out" + ColorAnsi.RESET);
        }

        out.prompt("\n" + ColorAnsi.GREEN_BOLD + "> " + ColorAnsi.RESET);
    }
}
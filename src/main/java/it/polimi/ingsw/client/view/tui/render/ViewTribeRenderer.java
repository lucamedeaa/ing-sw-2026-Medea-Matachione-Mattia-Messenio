package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import java.util.List;

public class ViewTribeRenderer {
    private final OutputPort out;

    public ViewTribeRenderer(OutputPort out) {
        this.out = out;
    }

    public void render(String targetPlayer, List<Integer> tribeCards) {
        out.clearScreen();
        out.print("════ MESOS — TRIBE INSPECTION ════\n");
        out.print("  " + targetPlayer.toUpperCase() + "'S TRIBE");

        CardBoxRenderer.printCardRow(out, tribeCards, false);

        out.print("\n");
        out.prompt("  Premi Q per tornare alla partita > ");
    }
}
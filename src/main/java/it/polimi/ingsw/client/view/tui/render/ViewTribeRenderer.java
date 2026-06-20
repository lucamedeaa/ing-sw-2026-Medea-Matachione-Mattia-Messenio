package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import java.util.List;

/**
 * Renderer for the screen that shows another player's tribe.
 */
public class ViewTribeRenderer {
    private final OutputPort out;

    /**
     * Creates a tribe inspection renderer.
     *
     * @param out output port used for printing
     */
    public ViewTribeRenderer(OutputPort out) {
        this.out = out;
    }

    /**
     * Renders the selected player's tribe.
     *
     * @param targetPlayer nickname of the inspected player
     * @param tribeCards card identifiers in the player's tribe
     */
    public void render(String targetPlayer, List<Integer> tribeCards) {
        out.clearScreen();
        out.print("════ MESOS — TRIBE INSPECTION ════\n");
        out.print("  " + targetPlayer.toUpperCase() + "'S TRIBE");

        CardBoxRenderer.printCardRow(out, tribeCards, false);

        out.print("\n");
        out.prompt("  Press Q to return to the game > ");
    }
}

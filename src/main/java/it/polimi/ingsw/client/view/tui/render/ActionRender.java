package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.visitor.ActionVisitor;

/**
 * Renderer for action DTOs shown in the in-game action list.
 */
public class ActionRender implements ActionVisitor {
    private final OutputPort out;

    /**
     * Creates an action renderer.
     *
     * @param out output port used to print action descriptions
     */
    public ActionRender(OutputPort out) {
        this.out = out;
    }

    /** {@inheritDoc} */
    @Override
    public void visit(PlaceTotemActionDto action) {
        out.print("Place Totem (Available spaces: " + action.availableTileIndices() + ")");
    }

    /** {@inheritDoc} */
    @Override
    public void visit(TakeCardActionDto action) {
        out.print("Take Card (Upper picks: " + action.upperRowPick() + ", Lower picks: " + action.lowerRowPick() + ")");
    }

    /** {@inheritDoc} */
    @Override
    public void visit(SkipActionDto action) {
        out.print("Skip phase/bonus");
    }

}

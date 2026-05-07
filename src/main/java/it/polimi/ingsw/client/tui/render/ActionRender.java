package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.OutputPort;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.actions.PlaceTotemActionDTO;
import it.polimi.ingsw.network.dto.actions.SkipActionDTO;
import it.polimi.ingsw.network.dto.actions.TakeCardActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;

public class ActionRender implements ActionVisitor {
    private final OutputPort out;

    public ActionRender(OutputPort out) {
        this.out = out;
    }

    @Override
    public void visit(PlaceTotemActionDTO action) {
        out.print("Place Totem (Available spaces: " + action.availableTileIndices() + ")");
    }

    @Override
    public void visit(TakeCardActionDTO action) {
        out.print("Take Card (Upper picks: " + action.upperRowPick() + ", Lower picks: " + action.lowerRowPick() + ")");
    }

    @Override
    public void visit(SkipActionDTO action) {
        out.print("Skip phase/bonus");
    }

    @Override
    public void visit(BoardDTO board) { }
}
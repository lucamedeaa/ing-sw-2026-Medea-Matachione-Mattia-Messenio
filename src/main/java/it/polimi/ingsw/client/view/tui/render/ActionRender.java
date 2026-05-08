package it.polimi.ingsw.client.view.tui.render;

import it.polimi.ingsw.client.view.tui.OutputPort;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.visitor.ActionVisitor;

public class ActionRender implements ActionVisitor {
    private final OutputPort out;

    public ActionRender(OutputPort out) {
        this.out = out;
    }

    @Override
    public void visit(PlaceTotemActionDto action) {
        out.print("Place Totem (Available spaces: " + action.availableTileIndices() + ")");
    }

    @Override
    public void visit(TakeCardActionDto action) {
        out.print("Take Card (Upper picks: " + action.upperRowPick() + ", Lower picks: " + action.lowerRowPick() + ")");
    }

    @Override
    public void visit(SkipActionDto action) {
        out.print("Skip phase/bonus");
    }

    @Override
    public void visit(BoardDto board) { }
}
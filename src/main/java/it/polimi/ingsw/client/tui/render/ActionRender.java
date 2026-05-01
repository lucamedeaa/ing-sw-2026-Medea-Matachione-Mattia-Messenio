package it.polimi.ingsw.client.tui.render;

import it.polimi.ingsw.client.tui.TUI;
import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.actions.PlaceTotemActionDTO;
import it.polimi.ingsw.network.dto.actions.SkipActionDTO;
import it.polimi.ingsw.network.dto.actions.TakeCardActionDTO;
import it.polimi.ingsw.network.visitor.ActionVisitor;

public class ActionRender implements ActionVisitor {
    private final TUI tui;

    public ActionRender(TUI tui) {
        this.tui = tui;
    }

    @Override
    public void visit(PlaceTotemActionDTO action) {
        tui.print("Place Totem (Available spaces: " + action.availableTileIndices() + ")");
    }

    @Override
    public void visit(TakeCardActionDTO action) {
        tui.print("Take Card (Upper picks: " + action.upperRowPick() + ", Lower picks: " + action.lowerRowPick() + ")");
    }

    @Override
    public void visit(SkipActionDTO action) {
        tui.print("Skip phase/bonus");
    }

    @Override
    public void visit(BoardDTO board) {
        // Ignored in this context
    }
}

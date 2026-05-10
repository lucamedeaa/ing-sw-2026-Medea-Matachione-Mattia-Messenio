package it.polimi.ingsw.client.view.gui.visitor;

import it.polimi.ingsw.client.view.gui.controllers.ActionsPanelController;
import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.visitor.ActionVisitor;

public class FxActionVisitor implements ActionVisitor {

    private final ActionsPanelController controller;

    public FxActionVisitor(ActionsPanelController controller) {
        this.controller = controller;
    }

    @Override
    public void visit(PlaceTotemActionDto action) {
        // Passa al controller quali posizioni del totem sono legali
        controller.enablePlaceTotem(action.availableTileIndices());
    }

    @Override
    public void visit(TakeCardActionDto action) {
        // Passa al controller quante prese sono permesse per ogni riga
        controller.enableTakeCard(action.upperRowPick(), action.lowerRowPick());
    }

    @Override
    public void visit(SkipActionDto action) {
        controller.enableSkip();
    }

    @Override
    public void visit(BoardDto board) {
        // Non utilizzato in questo contesto
    }
}
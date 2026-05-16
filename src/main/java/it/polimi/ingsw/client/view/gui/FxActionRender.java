package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.view.gui.controllers.ActionsPanelController;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.visitor.ActionVisitor;

public class FxActionRender implements ActionVisitor {

    private final ActionsPanelController controller;

    public FxActionRender(ActionsPanelController controller) {
        this.controller = controller;
    }

    @Override
    public void visit(TakeCardActionDto a) {
        controller.enableTakeCard(a.upperRowPick(), a.lowerRowPick());
    }

    @Override
    public void visit(PlaceTotemActionDto a) {
        controller.enablePlaceTotem(a.availableTileIndices());
    }

    @Override
    public void visit(SkipActionDto a) {
        controller.enableSkip();
    }

}
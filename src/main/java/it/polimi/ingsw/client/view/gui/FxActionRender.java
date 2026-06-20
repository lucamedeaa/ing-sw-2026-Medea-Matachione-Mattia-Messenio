package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.view.gui.controllers.ActionsPanelController;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;
import it.polimi.ingsw.common.visitor.ActionVisitor;

/**
 * JavaFX visitor that enables the matching action controls for an available action DTO.
 */
public class FxActionRender implements ActionVisitor {

    private final ActionsPanelController controller;

    /**
     * Creates an action renderer bound to the actions panel.
     *
     * @param controller actions panel controller to update
     */
    public FxActionRender(ActionsPanelController controller) {
        this.controller = controller;
    }

    /** {@inheritDoc} */
    @Override
    public void visit(TakeCardActionDto a) {
        controller.enableTakeCard(a.upperRowPick(), a.lowerRowPick());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(PlaceTotemActionDto a) {
        controller.enablePlaceTotem(a.availableTileIndices());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(SkipActionDto a) {
        controller.enableSkip();
    }

}

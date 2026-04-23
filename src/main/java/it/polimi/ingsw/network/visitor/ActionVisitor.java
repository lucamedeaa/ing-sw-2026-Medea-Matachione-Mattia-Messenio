package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.dto.actions.PlaceTotemActionDTO;
import it.polimi.ingsw.network.dto.actions.SkipActionDTO;
import it.polimi.ingsw.network.dto.actions.TakeCardActionDTO;

public interface ActionVisitor {
    void visit(PlaceTotemActionDTO action);
    void visit(TakeCardActionDTO action);
    void visit(SkipActionDTO action);

}

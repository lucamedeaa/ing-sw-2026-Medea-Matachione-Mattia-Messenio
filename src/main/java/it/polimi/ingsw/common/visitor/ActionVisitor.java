package it.polimi.ingsw.common.visitor;

import it.polimi.ingsw.common.network.dto.BoardDto;
import it.polimi.ingsw.common.network.dto.action.PlaceTotemActionDto;
import it.polimi.ingsw.common.network.dto.action.SkipActionDto;
import it.polimi.ingsw.common.network.dto.action.TakeCardActionDto;

public interface ActionVisitor {
    void visit(PlaceTotemActionDto action);
    void visit(TakeCardActionDto action);
    void visit(SkipActionDto action);
    void visit(BoardDto board);

}
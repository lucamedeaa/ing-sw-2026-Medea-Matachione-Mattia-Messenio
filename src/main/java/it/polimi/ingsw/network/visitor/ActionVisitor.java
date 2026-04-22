package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.dto.BoardDTO;
import it.polimi.ingsw.network.dto.GameEventDTO;
import it.polimi.ingsw.network.dto.actions.PlaceTotemActionDTO;
import it.polimi.ingsw.network.dto.actions.SkipBonusActionDTO;
import it.polimi.ingsw.network.dto.actions.TakeCardActionDTO;
import it.polimi.ingsw.network.dto.events.CardTakenEventDTO;

public interface ActionVisitor {
    void visit(PlaceTotemActionDTO action);
    void visit(TakeCardActionDTO action);
    void visit(SkipBonusActionDTO action);

}

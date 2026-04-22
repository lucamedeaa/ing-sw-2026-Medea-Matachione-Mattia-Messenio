package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.dto.events.*;

public interface EventVisitor {
    void visit(CardTakenEventDTO event);
    void visit(BoardRefilledEventDTO event);
    void visit(TotemPlacedEventDTO event);
    void visit(PlayerResourcesChangedEventDTO event);
    void visit(CardAddedToTribeEventDTO event);
    void visit(EraTransitionEventDTO event);
    void visit(RoundAdvancedEventDTO event);
}
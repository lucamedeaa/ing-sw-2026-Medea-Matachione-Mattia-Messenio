package it.polimi.ingsw.common.visitor;

import it.polimi.ingsw.common.network.dto.TribeDto;
import it.polimi.ingsw.common.network.dto.event.*;

public interface EventVisitor {
    void visit(CardTakenDto event);
    void visit(BoardRefilledDto event);
    void visit(TotemPlacedDto event);
    void visit(PlayerResourcesChangedDto event);
    void visit(CardAddedToTribeDto event);
    void visit(EraTransitionDto event);
    void visit(RoundAdvancedDto event);
    void visit(TribeDto tribe);
    void visit(WinnersAnnouncedDto event);
    void visit(PlayerLeftGameDto event);
    void visit(GameOverDto event);
    void visit(TotemReturnedDto event);
}

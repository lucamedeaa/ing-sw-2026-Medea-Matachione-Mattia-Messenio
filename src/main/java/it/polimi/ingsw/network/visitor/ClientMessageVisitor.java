package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.messages.AvailableGamesResponseMessage;
import it.polimi.ingsw.network.messages.DeltaEventMessage;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.ErrorMessageDTO;
import it.polimi.ingsw.network.messages.FullSyncMessage;
import it.polimi.ingsw.network.messages.MatchmakingSuccessMessage;

public interface ClientMessageVisitor {
    void visit(FullSyncMessage message);
    void visit(DeltaEventMessage message);
    void visit(ErrorMessage message);
    void visit(ErrorMessageDTO message);
    void visit(MatchmakingSuccessMessage message);
    void visit(AvailableGamesResponseMessage message);
}
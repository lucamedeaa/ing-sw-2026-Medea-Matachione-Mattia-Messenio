package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.messages.*;


public interface ClientMessageVisitor {
    void visit(FullSyncMessage message);
    void visit(DeltaEventMessage message);
    void visit(ErrorMessage message);
    void visit(ErrorMessageDTO message);
    void visit(MatchmakingSuccessMessage message);
    void visit(AvailableGamesResponseMessage message);
    void visit(GameAbortedMessage message);
    void visit(RoomUpdateMessage message);
    void visit(GameLeftSuccessMessage message);
}
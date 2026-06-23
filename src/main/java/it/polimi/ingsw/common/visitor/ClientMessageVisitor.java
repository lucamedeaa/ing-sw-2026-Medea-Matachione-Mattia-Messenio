package it.polimi.ingsw.common.visitor;

import it.polimi.ingsw.common.network.dto.event.ErrorDto;
import it.polimi.ingsw.common.message.server.*;


/** Defines the contract for client message visitor. */
public interface ClientMessageVisitor {
    void visit(FullSyncMessage message);
    void visit(DeltaEventMessage message);
    void visit(ErrorMessage message);
    void visit(ErrorDto message);
    void visit(MatchmakingSuccessMessage message);
    void visit(AvailableGamesResponseMessage message);
    void visit(GameAbortedMessage message);
    void visit(RoomUpdateMessage message);
    void visit(GameLeftSuccessMessage message);
    void visit(GameCompletedMessage message);
    void visit(LeaderboardResponseMessage message);
    void visit(ServerDisconnectedMessage message);
}

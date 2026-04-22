package it.polimi.ingsw.network.visitor;

import it.polimi.ingsw.network.messages.DeltaEventMessage;
import it.polimi.ingsw.network.messages.FullSyncMessage;

public interface ClientMessageVisitor {
    void visit(FullSyncMessage message);
    void visit(DeltaEventMessage message);
}

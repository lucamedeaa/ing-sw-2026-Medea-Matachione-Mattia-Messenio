package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.server.ConnectionState;
import it.polimi.ingsw.network.visitor.AfterGameVisitor;

public interface AfterGameMessage extends ClientMessage {
    void accept(AfterGameVisitor visitor);

    @Override
    default void dispatchTo(ConnectionState state) {
        state.handle(this);
    }
}

package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.server.ConnectionState;
import it.polimi.ingsw.network.visitor.MatchmakingVisitor;

public interface MatchmakingMessage extends ClientMessage {
    void accept(MatchmakingVisitor visitor);

    @Override
    default void dispatchTo(ConnectionState state) {
        state.handle(this);
    }
}
